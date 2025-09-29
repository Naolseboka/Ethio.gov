package et.gov.ethgov.workflow.application.assignment;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListAssignmentUseCase {
    private final AssignmentRepository repository;

    public ListAssignmentUseCase(AssignmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Assignment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
