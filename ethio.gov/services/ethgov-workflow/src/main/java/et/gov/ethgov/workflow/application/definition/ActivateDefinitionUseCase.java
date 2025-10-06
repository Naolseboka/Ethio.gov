package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionId;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDefinitionUseCase {
    private final DefinitionRepository repository;
    private final Clock clock;

    public ActivateDefinitionUseCase(DefinitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DefinitionId execute(ActivateDefinitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DefinitionId id = DefinitionId.parse(command.definitionId());
        Definition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("definition not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDefinitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String definitionId,
            String reason) {}
}
