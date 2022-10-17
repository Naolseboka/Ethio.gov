package et.gov.ethgov.notification.application.campaign;

import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignId;
import et.gov.ethgov.notification.domain.campaign.CampaignRepository;
import java.util.Objects;

public final class GetCampaignUseCase {
    private final CampaignRepository repository;

    public GetCampaignUseCase(CampaignRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Campaign execute(String rawId) {
        CampaignId id = CampaignId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("campaign not found: " + rawId));
    }
}
