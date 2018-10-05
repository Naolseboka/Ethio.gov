package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateScorecardUseCase {
    private final ScorecardRepository repository;
    private final Clock clock;

    public CreateScorecardUseCase(ScorecardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScorecardId execute(CreateScorecardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Scorecard aggregate = Scorecard.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateScorecardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scorecardId,
            String reason) {}
}
