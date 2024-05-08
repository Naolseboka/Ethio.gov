package et.gov.ethgov.permits.application.sitevisit;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitId;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSitevisitUseCase {
    private final SitevisitRepository repository;
    private final Clock clock;

    public CreateSitevisitUseCase(SitevisitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SitevisitId execute(CreateSitevisitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Sitevisit aggregate = Sitevisit.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSitevisitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sitevisitId,
            String reason) {}
}
