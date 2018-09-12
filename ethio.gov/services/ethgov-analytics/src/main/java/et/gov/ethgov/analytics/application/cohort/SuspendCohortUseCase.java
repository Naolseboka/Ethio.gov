package et.gov.ethgov.analytics.application.cohort;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortId;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCohortUseCase {
    private final CohortRepository repository;
    private final Clock clock;

    public SuspendCohortUseCase(CohortRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CohortId execute(SuspendCohortCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CohortId id = CohortId.parse(command.cohortId());
        Cohort aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cohort not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCohortCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cohortId,
            String reason) {}
}
