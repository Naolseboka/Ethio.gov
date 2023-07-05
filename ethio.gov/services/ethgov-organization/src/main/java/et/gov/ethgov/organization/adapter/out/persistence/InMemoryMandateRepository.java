package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateId;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import et.gov.ethgov.organization.domain.mandate.MandateStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Mandate (replaceable with JPA). */
public final class InMemoryMandateRepository implements MandateRepository {
    private final Map<UUID, StoredMandate> store = new ConcurrentHashMap<>();

    @Override
    public Mandate save(Mandate aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredMandate.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Mandate> findById(MandateId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredMandate::toDomain);
    }

    @Override
    public Optional<Mandate> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredMandate::toDomain)
                .findFirst();
    }

    @Override
    public List<Mandate> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredMandate row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredMandate::toDomain)
                .toList();
    }

    @Override
    public void delete(MandateId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredMandate(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredMandate from(Mandate aggregate) {
            return new StoredMandate(
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

        Mandate toDomain() {
            return Mandate.rehydrate(
                    MandateId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    MandateStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
