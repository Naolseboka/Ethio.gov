package et.gov.ethgov.revenue.application.assessment;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListAssessmentUseCase {
    private final AssessmentRepository repository;

    public ListAssessmentUseCase(AssessmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Assessment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
