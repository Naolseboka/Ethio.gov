package et.gov.ethgov.case.application.caseassignment;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListCaseassignmentUseCase {
    private final CaseassignmentRepository repository;

    public ListCaseassignmentUseCase(CaseassignmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Caseassignment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
