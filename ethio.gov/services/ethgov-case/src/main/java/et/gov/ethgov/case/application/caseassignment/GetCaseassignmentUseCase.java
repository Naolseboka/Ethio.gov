package et.gov.ethgov.case.application.caseassignment;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentId;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import java.util.Objects;

public final class GetCaseassignmentUseCase {
    private final CaseassignmentRepository repository;

    public GetCaseassignmentUseCase(CaseassignmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Caseassignment execute(String rawId) {
        CaseassignmentId id = CaseassignmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("caseassignment not found: " + rawId));
    }
}
