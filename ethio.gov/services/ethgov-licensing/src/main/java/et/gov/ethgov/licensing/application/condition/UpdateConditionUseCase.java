package et.gov.ethgov.licensing.application.condition;

import et.gov.ethgov.licensing.domain.condition.Condition;
import et.gov.ethgov.licensing.domain.condition.ConditionId;
import et.gov.ethgov.licensing.domain.condition.ConditionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateConditionUseCase {
    private final ConditionRepository repository;
    private final Clock clock;

    public UpdateConditionUseCase(ConditionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConditionId execute(UpdateConditionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ConditionId id = ConditionId.parse(command.conditionId());
        Condition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("condition not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateConditionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String conditionId,
            String reason) {}
}
