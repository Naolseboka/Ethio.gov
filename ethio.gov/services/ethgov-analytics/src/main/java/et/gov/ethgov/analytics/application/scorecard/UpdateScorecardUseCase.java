package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateScorecardUseCase {
    private final ScorecardRepository repository;
    private final Clock clock;

    public UpdateScorecardUseCase(ScorecardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScorecardId execute(UpdateScorecardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ScorecardId id = ScorecardId.parse(command.scorecardId());
        Scorecard aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scorecard not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateScorecardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scorecardId,
            String reason) {}
}
