package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationId;
import et.gov.ethgov.licensing.domain.application.ApplicationRepository;
import et.gov.ethgov.licensing.domain.application.ApplicationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Application (replaceable with JPA). */
public final class InMemoryApplicationRepository implements ApplicationRepository {
    private final Map<UUID, StoredApplication> store = new ConcurrentHashMap<>();

    @Override
    public Application save(Application aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredApplication.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Application> findById(ApplicationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredApplication::toDomain);
    }

    @Override
    public Optional<Application> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredApplication::toDomain)
                .findFirst();
    }

    @Override
    public List<Application> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredApplication row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredApplication::toDomain)
                .toList();
    }

    @Override
    public void delete(ApplicationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredApplication(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredApplication from(Application aggregate) {
            return new StoredApplication(
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

        Application toDomain() {
            return Application.rehydrate(
                    ApplicationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ApplicationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
