package et.gov.ethgov.identity.application.riskscore;

import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreId;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRiskscoreUseCase {
    private final RiskscoreRepository repository;
    private final Clock clock;

    public CreateRiskscoreUseCase(RiskscoreRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RiskscoreId execute(CreateRiskscoreCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Riskscore aggregate = Riskscore.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRiskscoreCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String riskscoreId,
            String reason) {}
}
