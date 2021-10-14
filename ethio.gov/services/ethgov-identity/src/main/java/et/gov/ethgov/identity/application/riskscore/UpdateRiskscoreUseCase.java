package et.gov.ethgov.identity.application.riskscore;

import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreId;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRiskscoreUseCase {
    private final RiskscoreRepository repository;
    private final Clock clock;

    public UpdateRiskscoreUseCase(RiskscoreRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RiskscoreId execute(UpdateRiskscoreCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RiskscoreId id = RiskscoreId.parse(command.riskscoreId());
        Riskscore aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("riskscore not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRiskscoreCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String riskscoreId,
            String reason) {}
}
