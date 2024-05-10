package et.gov.ethgov.permits.application.sitevisit;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitId;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSitevisitUseCase {
    private final SitevisitRepository repository;
    private final Clock clock;

    public SuspendSitevisitUseCase(SitevisitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SitevisitId execute(SuspendSitevisitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SitevisitId id = SitevisitId.parse(command.sitevisitId());
        Sitevisit aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sitevisit not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSitevisitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sitevisitId,
            String reason) {}
}
