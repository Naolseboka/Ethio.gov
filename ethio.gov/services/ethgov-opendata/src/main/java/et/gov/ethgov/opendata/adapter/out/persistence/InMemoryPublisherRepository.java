package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherId;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import et.gov.ethgov.opendata.domain.publisher.PublisherStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Publisher (replaceable with JPA). */
public final class InMemoryPublisherRepository implements PublisherRepository {
    private final Map<UUID, StoredPublisher> store = new ConcurrentHashMap<>();

    @Override
    public Publisher save(Publisher aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPublisher.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Publisher> findById(PublisherId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPublisher::toDomain);
    }

    @Override
    public Optional<Publisher> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPublisher::toDomain)
                .findFirst();
    }

    @Override
    public List<Publisher> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPublisher row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPublisher::toDomain)
                .toList();
    }

    @Override
    public void delete(PublisherId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPublisher(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPublisher from(Publisher aggregate) {
            return new StoredPublisher(
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

        Publisher toDomain() {
            return Publisher.rehydrate(
                    PublisherId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PublisherStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
