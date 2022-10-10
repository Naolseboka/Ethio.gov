package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignId;
import et.gov.ethgov.notification.domain.campaign.CampaignRepository;
import et.gov.ethgov.notification.domain.campaign.CampaignStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Campaign (replaceable with JPA). */
public final class InMemoryCampaignRepository implements CampaignRepository {
    private final Map<UUID, StoredCampaign> store = new ConcurrentHashMap<>();

    @Override
    public Campaign save(Campaign aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCampaign.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Campaign> findById(CampaignId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCampaign::toDomain);
    }

    @Override
    public Optional<Campaign> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCampaign::toDomain)
                .findFirst();
    }

    @Override
    public List<Campaign> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCampaign row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCampaign::toDomain)
                .toList();
    }

    @Override
    public void delete(CampaignId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCampaign(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCampaign from(Campaign aggregate) {
            return new StoredCampaign(
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

        Campaign toDomain() {
            return Campaign.rehydrate(
                    CampaignId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CampaignStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
