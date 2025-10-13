package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionId;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDefinitionUseCase {
    private final DefinitionRepository repository;
    private final Clock clock;

    public SuspendDefinitionUseCase(DefinitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DefinitionId execute(SuspendDefinitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DefinitionId id = DefinitionId.parse(command.definitionId());
        Definition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("definition not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDefinitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String definitionId,
            String reason) {}
}
