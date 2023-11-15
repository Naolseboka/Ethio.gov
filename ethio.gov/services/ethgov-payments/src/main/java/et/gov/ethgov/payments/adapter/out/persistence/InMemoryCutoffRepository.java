package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffId;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import et.gov.ethgov.payments.domain.cutoff.CutoffStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Cutoff (replaceable with JPA). */
public final class InMemoryCutoffRepository implements CutoffRepository {
    private final Map<UUID, StoredCutoff> store = new ConcurrentHashMap<>();

    @Override
    public Cutoff save(Cutoff aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCutoff.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Cutoff> findById(CutoffId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCutoff::toDomain);
    }

    @Override
    public Optional<Cutoff> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCutoff::toDomain)
                .findFirst();
    }

    @Override
    public List<Cutoff> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCutoff row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCutoff::toDomain)
                .toList();
    }

    @Override
    public void delete(CutoffId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCutoff(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCutoff from(Cutoff aggregate) {
            return new StoredCutoff(
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

        Cutoff toDomain() {
            return Cutoff.rehydrate(
                    CutoffId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CutoffStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
