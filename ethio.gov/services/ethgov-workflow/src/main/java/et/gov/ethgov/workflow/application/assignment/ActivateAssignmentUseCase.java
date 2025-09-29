package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAssignmentUseCase {
    private final AssignmentRepository repository;
    private final Clock clock;

    public ActivateAssignmentUseCase(AssignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssignmentId execute(ActivateAssignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AssignmentId id = AssignmentId.parse(command.assignmentId());
        Assignment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assignment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAssignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assignmentId,
            String reason) {}
}
