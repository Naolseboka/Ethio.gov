package et.gov.ethgov.analytics.application.cohort;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortId;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCohortUseCase {
    private final CohortRepository repository;
    private final Clock clock;

    public CreateCohortUseCase(CohortRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CohortId execute(CreateCohortCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Cohort aggregate = Cohort.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCohortCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cohortId,
            String reason) {}
}
