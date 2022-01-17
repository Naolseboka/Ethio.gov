package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleId;
import et.gov.ethgov.land.domain.title.TitleRepository;
import et.gov.ethgov.land.domain.title.TitleStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Title (replaceable with JPA). */
public final class InMemoryTitleRepository implements TitleRepository {
    private final Map<UUID, StoredTitle> store = new ConcurrentHashMap<>();

    @Override
    public Title save(Title aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTitle.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Title> findById(TitleId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTitle::toDomain);
    }

    @Override
    public Optional<Title> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTitle::toDomain)
                .findFirst();
    }

    @Override
    public List<Title> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTitle row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTitle::toDomain)
                .toList();
    }

    @Override
    public void delete(TitleId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTitle(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTitle from(Title aggregate) {
            return new StoredTitle(
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

        Title toDomain() {
            return Title.rehydrate(
                    TitleId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TitleStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
