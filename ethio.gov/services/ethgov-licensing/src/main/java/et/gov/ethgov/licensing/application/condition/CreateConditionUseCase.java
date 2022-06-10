package et.gov.ethgov.licensing.application.condition;

import et.gov.ethgov.licensing.domain.condition.Condition;
import et.gov.ethgov.licensing.domain.condition.ConditionId;
import et.gov.ethgov.licensing.domain.condition.ConditionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateConditionUseCase {
    private final ConditionRepository repository;
    private final Clock clock;

    public CreateConditionUseCase(ConditionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConditionId execute(CreateConditionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Condition aggregate = Condition.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateConditionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String conditionId,
            String reason) {}
}
