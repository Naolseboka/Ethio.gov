package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayId;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import et.gov.ethgov.gis.domain.overlay.OverlayStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Overlay (replaceable with JPA). */
public final class InMemoryOverlayRepository implements OverlayRepository {
    private final Map<UUID, StoredOverlay> store = new ConcurrentHashMap<>();

    @Override
    public Overlay save(Overlay aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOverlay.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Overlay> findById(OverlayId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOverlay::toDomain);
    }

    @Override
    public Optional<Overlay> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOverlay::toDomain)
                .findFirst();
    }

    @Override
    public List<Overlay> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOverlay row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOverlay::toDomain)
                .toList();
    }

    @Override
    public void delete(OverlayId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOverlay(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOverlay from(Overlay aggregate) {
            return new StoredOverlay(
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

        Overlay toDomain() {
            return Overlay.rehydrate(
                    OverlayId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OverlayStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
