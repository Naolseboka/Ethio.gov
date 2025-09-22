package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeId;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeRepository;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Gatewaynode (replaceable with JPA). */
public final class InMemoryGatewaynodeRepository implements GatewaynodeRepository {
    private final Map<UUID, StoredGatewaynode> store = new ConcurrentHashMap<>();

    @Override
    public Gatewaynode save(Gatewaynode aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredGatewaynode.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Gatewaynode> findById(GatewaynodeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredGatewaynode::toDomain);
    }

    @Override
    public Optional<Gatewaynode> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredGatewaynode::toDomain)
                .findFirst();
    }

    @Override
    public List<Gatewaynode> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredGatewaynode row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredGatewaynode::toDomain)
                .toList();
    }

    @Override
    public void delete(GatewaynodeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredGatewaynode(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredGatewaynode from(Gatewaynode aggregate) {
            return new StoredGatewaynode(
                    aggregate.id().value(),
                    aggregate.referenceCode(),
                    aggregate.title(),
                    aggregate.description(),
                    aggregate.tenantId(),
                    aggregate.status().name(),
                    aggregate.createdAt(),
                    aggregate.updatedAt(),
                    aggregate.version());
        }

        Gatewaynode toDomain() {
            return Gatewaynode.rehydrate(
                    GatewaynodeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    GatewaynodeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
