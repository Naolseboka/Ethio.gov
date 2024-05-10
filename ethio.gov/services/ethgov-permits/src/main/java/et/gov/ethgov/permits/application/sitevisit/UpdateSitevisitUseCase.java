package et.gov.ethgov.permits.application.sitevisit;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitId;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSitevisitUseCase {
    private final SitevisitRepository repository;
    private final Clock clock;

    public UpdateSitevisitUseCase(SitevisitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SitevisitId execute(UpdateSitevisitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SitevisitId id = SitevisitId.parse(command.sitevisitId());
        Sitevisit aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sitevisit not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSitevisitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sitevisitId,
            String reason) {}
}
