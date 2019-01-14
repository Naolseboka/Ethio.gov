package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileId;
import et.gov.ethgov.case.domain.casefile.CasefileRepository;
import et.gov.ethgov.case.domain.casefile.CasefileStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Casefile (replaceable with JPA). */
public final class InMemoryCasefileRepository implements CasefileRepository {
    private final Map<UUID, StoredCasefile> store = new ConcurrentHashMap<>();

    @Override
    public Casefile save(Casefile aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCasefile.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Casefile> findById(CasefileId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCasefile::toDomain);
    }

    @Override
    public Optional<Casefile> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCasefile::toDomain)
                .findFirst();
    }

    @Override
    public List<Casefile> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCasefile row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCasefile::toDomain)
                .toList();
    }

    @Override
    public void delete(CasefileId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCasefile(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCasefile from(Casefile aggregate) {
            return new StoredCasefile(
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

        Casefile toDomain() {
            return Casefile.rehydrate(
                    CasefileId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CasefileStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
