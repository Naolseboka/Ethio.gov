package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionId;
import et.gov.ethgov.search.domain.suggestion.SuggestionRepository;
import et.gov.ethgov.search.domain.suggestion.SuggestionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Suggestion (replaceable with JPA). */
public final class InMemorySuggestionRepository implements SuggestionRepository {
    private final Map<UUID, StoredSuggestion> store = new ConcurrentHashMap<>();

    @Override
    public Suggestion save(Suggestion aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSuggestion.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Suggestion> findById(SuggestionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSuggestion::toDomain);
    }

    @Override
    public Optional<Suggestion> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSuggestion::toDomain)
                .findFirst();
    }

    @Override
    public List<Suggestion> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSuggestion row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSuggestion::toDomain)
                .toList();
    }

    @Override
    public void delete(SuggestionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSuggestion(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSuggestion from(Suggestion aggregate) {
            return new StoredSuggestion(
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

        Suggestion toDomain() {
            return Suggestion.rehydrate(
                    SuggestionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SuggestionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
