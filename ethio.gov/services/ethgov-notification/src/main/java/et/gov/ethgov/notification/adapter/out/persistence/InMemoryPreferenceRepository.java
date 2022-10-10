package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceId;
import et.gov.ethgov.notification.domain.preference.PreferenceRepository;
import et.gov.ethgov.notification.domain.preference.PreferenceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Preference (replaceable with JPA). */
public final class InMemoryPreferenceRepository implements PreferenceRepository {
    private final Map<UUID, StoredPreference> store = new ConcurrentHashMap<>();

    @Override
    public Preference save(Preference aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPreference.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Preference> findById(PreferenceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPreference::toDomain);
    }

    @Override
    public Optional<Preference> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPreference::toDomain)
                .findFirst();
    }

    @Override
    public List<Preference> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPreference row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPreference::toDomain)
                .toList();
    }

    @Override
    public void delete(PreferenceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPreference(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPreference from(Preference aggregate) {
            return new StoredPreference(
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

        Preference toDomain() {
            return Preference.rehydrate(
                    PreferenceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PreferenceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
