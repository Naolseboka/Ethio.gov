package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.util.Objects;

public final class GetAssignmentUseCase {
    private final AssignmentRepository repository;

    public GetAssignmentUseCase(AssignmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Assignment execute(String rawId) {
        AssignmentId id = AssignmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assignment not found: " + rawId));
    }
}
