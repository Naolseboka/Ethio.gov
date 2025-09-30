package et.gov.ethgov.workflow.application.checklist;

import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistId;
import et.gov.ethgov.workflow.domain.checklist.ChecklistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateChecklistUseCase {
    private final ChecklistRepository repository;
    private final Clock clock;

    public CreateChecklistUseCase(ChecklistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChecklistId execute(CreateChecklistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Checklist aggregate = Checklist.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateChecklistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String checklistId,
            String reason) {}
}
