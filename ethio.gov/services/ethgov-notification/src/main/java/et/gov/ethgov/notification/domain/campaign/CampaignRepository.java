package et.gov.ethgov.notification.domain.campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Campaign aggregate);

    Optional<Campaign> findById(CampaignId id);

    Optional<Campaign> findByReferenceCode(String tenantId, String referenceCode);

    List<Campaign> findByTenant(String tenantId, int limit);

    void delete(CampaignId id);
}
