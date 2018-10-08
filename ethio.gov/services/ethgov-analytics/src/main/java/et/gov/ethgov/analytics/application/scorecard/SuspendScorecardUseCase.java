package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendScorecardUseCase {
    private final ScorecardRepository repository;
    private final Clock clock;

    public SuspendScorecardUseCase(ScorecardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScorecardId execute(SuspendScorecardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ScorecardId id = ScorecardId.parse(command.scorecardId());
        Scorecard aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scorecard not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendScorecardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scorecardId,
            String reason) {}
}
