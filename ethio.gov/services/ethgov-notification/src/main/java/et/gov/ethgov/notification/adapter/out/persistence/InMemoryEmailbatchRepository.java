package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchId;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchRepository;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Emailbatch (replaceable with JPA). */
public final class InMemoryEmailbatchRepository implements EmailbatchRepository {
    private final Map<UUID, StoredEmailbatch> store = new ConcurrentHashMap<>();

    @Override
    public Emailbatch save(Emailbatch aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEmailbatch.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Emailbatch> findById(EmailbatchId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEmailbatch::toDomain);
    }

    @Override
    public Optional<Emailbatch> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEmailbatch::toDomain)
                .findFirst();
    }

    @Override
    public List<Emailbatch> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEmailbatch row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEmailbatch::toDomain)
                .toList();
    }

    @Override
    public void delete(EmailbatchId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEmailbatch(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEmailbatch from(Emailbatch aggregate) {
            return new StoredEmailbatch(
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

        Emailbatch toDomain() {
            return Emailbatch.rehydrate(
                    EmailbatchId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EmailbatchStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
