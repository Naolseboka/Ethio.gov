package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionId;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDefinitionUseCase {
    private final DefinitionRepository repository;
    private final Clock clock;

    public CreateDefinitionUseCase(DefinitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DefinitionId execute(CreateDefinitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Definition aggregate = Definition.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDefinitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String definitionId,
            String reason) {}
}
