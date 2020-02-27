package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursId;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursRepository;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Openinghours (replaceable with JPA). */
public final class InMemoryOpeninghoursRepository implements OpeninghoursRepository {
    private final Map<UUID, StoredOpeninghours> store = new ConcurrentHashMap<>();

    @Override
    public Openinghours save(Openinghours aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOpeninghours.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Openinghours> findById(OpeninghoursId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOpeninghours::toDomain);
    }

    @Override
    public Optional<Openinghours> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOpeninghours::toDomain)
                .findFirst();
    }

    @Override
    public List<Openinghours> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOpeninghours row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOpeninghours::toDomain)
                .toList();
    }

    @Override
    public void delete(OpeninghoursId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOpeninghours(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOpeninghours from(Openinghours aggregate) {
            return new StoredOpeninghours(
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

        Openinghours toDomain() {
            return Openinghours.rehydrate(
                    OpeninghoursId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OpeninghoursStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
