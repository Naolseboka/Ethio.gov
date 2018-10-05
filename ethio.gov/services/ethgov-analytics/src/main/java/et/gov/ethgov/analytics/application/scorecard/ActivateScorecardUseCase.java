package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateScorecardUseCase {
    private final ScorecardRepository repository;
    private final Clock clock;

    public ActivateScorecardUseCase(ScorecardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScorecardId execute(ActivateScorecardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ScorecardId id = ScorecardId.parse(command.scorecardId());
        Scorecard aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scorecard not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateScorecardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scorecardId,
            String reason) {}
}
