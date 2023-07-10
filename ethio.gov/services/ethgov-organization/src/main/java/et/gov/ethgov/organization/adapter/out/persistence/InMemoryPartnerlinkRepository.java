package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkId;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Partnerlink (replaceable with JPA). */
public final class InMemoryPartnerlinkRepository implements PartnerlinkRepository {
    private final Map<UUID, StoredPartnerlink> store = new ConcurrentHashMap<>();

    @Override
    public Partnerlink save(Partnerlink aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPartnerlink.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Partnerlink> findById(PartnerlinkId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPartnerlink::toDomain);
    }

    @Override
    public Optional<Partnerlink> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPartnerlink::toDomain)
                .findFirst();
    }

    @Override
    public List<Partnerlink> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPartnerlink row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPartnerlink::toDomain)
                .toList();
    }

    @Override
    public void delete(PartnerlinkId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPartnerlink(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPartnerlink from(Partnerlink aggregate) {
            return new StoredPartnerlink(
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

        Partnerlink toDomain() {
            return Partnerlink.rehydrate(
                    PartnerlinkId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PartnerlinkStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
