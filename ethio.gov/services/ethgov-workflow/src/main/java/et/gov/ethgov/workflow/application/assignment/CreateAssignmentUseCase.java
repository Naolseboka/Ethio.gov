package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAssignmentUseCase {
    private final AssignmentRepository repository;
    private final Clock clock;

    public CreateAssignmentUseCase(AssignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssignmentId execute(CreateAssignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Assignment aggregate = Assignment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAssignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assignmentId,
            String reason) {}
}
