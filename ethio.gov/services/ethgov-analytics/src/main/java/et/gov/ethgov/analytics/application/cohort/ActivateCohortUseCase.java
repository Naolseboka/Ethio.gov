package et.gov.ethgov.analytics.application.cohort;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortId;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCohortUseCase {
    private final CohortRepository repository;
    private final Clock clock;

    public ActivateCohortUseCase(CohortRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CohortId execute(ActivateCohortCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CohortId id = CohortId.parse(command.cohortId());
        Cohort aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cohort not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCohortCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cohortId,
            String reason) {}
}
