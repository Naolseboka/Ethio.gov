package et.gov.ethgov.notification.application.campaign;

import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignId;
import et.gov.ethgov.notification.domain.campaign.CampaignRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCampaignUseCase {
    private final CampaignRepository repository;
    private final Clock clock;

    public ActivateCampaignUseCase(CampaignRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CampaignId execute(ActivateCampaignCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CampaignId id = CampaignId.parse(command.campaignId());
        Campaign aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("campaign not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCampaignCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String campaignId,
            String reason) {}
}
