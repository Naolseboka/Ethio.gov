package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAssignmentUseCase {
    private final AssignmentRepository repository;
    private final Clock clock;

    public UpdateAssignmentUseCase(AssignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssignmentId execute(UpdateAssignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AssignmentId id = AssignmentId.parse(command.assignmentId());
        Assignment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assignment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAssignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assignmentId,
            String reason) {}
}
