package et.gov.ethgov.notification.application.campaign;

import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignRepository;
import java.util.List;
import java.util.Objects;

public final class ListCampaignUseCase {
    private final CampaignRepository repository;

    public ListCampaignUseCase(CampaignRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Campaign> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
