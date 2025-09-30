package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendAssignmentUseCase {
    private final AssignmentRepository repository;
    private final Clock clock;

    public SuspendAssignmentUseCase(AssignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssignmentId execute(SuspendAssignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AssignmentId id = AssignmentId.parse(command.assignmentId());
        Assignment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assignment not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendAssignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assignmentId,
            String reason) {}
}
