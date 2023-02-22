package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagId;
import et.gov.ethgov.opendata.domain.tag.TagRepository;
import et.gov.ethgov.opendata.domain.tag.TagStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Tag (replaceable with JPA). */
public final class InMemoryTagRepository implements TagRepository {
    private final Map<UUID, StoredTag> store = new ConcurrentHashMap<>();

    @Override
    public Tag save(Tag aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTag.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Tag> findById(TagId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTag::toDomain);
    }

    @Override
    public Optional<Tag> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTag::toDomain)
                .findFirst();
    }

    @Override
    public List<Tag> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTag row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTag::toDomain)
                .toList();
    }

    @Override
    public void delete(TagId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTag(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTag from(Tag aggregate) {
            return new StoredTag(
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

        Tag toDomain() {
            return Tag.rehydrate(
                    TagId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TagStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
