package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastId;
import et.gov.ethgov.analytics.domain.forecast.ForecastRepository;
import et.gov.ethgov.analytics.domain.forecast.ForecastStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Forecast (replaceable with JPA). */
public final class InMemoryForecastRepository implements ForecastRepository {
    private final Map<UUID, StoredForecast> store = new ConcurrentHashMap<>();

    @Override
    public Forecast save(Forecast aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredForecast.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Forecast> findById(ForecastId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredForecast::toDomain);
    }

    @Override
    public Optional<Forecast> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredForecast::toDomain)
                .findFirst();
    }

    @Override
    public List<Forecast> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredForecast row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredForecast::toDomain)
                .toList();
    }

    @Override
    public void delete(ForecastId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredForecast(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredForecast from(Forecast aggregate) {
            return new StoredForecast(
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

        Forecast toDomain() {
            return Forecast.rehydrate(
                    ForecastId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ForecastStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
