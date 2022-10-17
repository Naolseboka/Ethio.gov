package et.gov.ethgov.notification.application.campaign;

import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignId;
import et.gov.ethgov.notification.domain.campaign.CampaignRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCampaignUseCase {
    private final CampaignRepository repository;
    private final Clock clock;

    public CreateCampaignUseCase(CampaignRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CampaignId execute(CreateCampaignCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Campaign aggregate = Campaign.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCampaignCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String campaignId,
            String reason) {}
}
