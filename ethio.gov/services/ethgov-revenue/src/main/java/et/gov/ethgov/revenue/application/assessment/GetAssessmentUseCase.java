package et.gov.ethgov.revenue.application.assessment;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentId;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import java.util.Objects;

public final class GetAssessmentUseCase {
    private final AssessmentRepository repository;

    public GetAssessmentUseCase(AssessmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Assessment execute(String rawId) {
        AssessmentId id = AssessmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assessment not found: " + rawId));
    }
}
