package et.gov.ethgov.workflow.application.checklist;

import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistId;
import et.gov.ethgov.workflow.domain.checklist.ChecklistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateChecklistUseCase {
    private final ChecklistRepository repository;
    private final Clock clock;

    public UpdateChecklistUseCase(ChecklistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChecklistId execute(UpdateChecklistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ChecklistId id = ChecklistId.parse(command.checklistId());
        Checklist aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("checklist not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateChecklistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String checklistId,
            String reason) {}
}
