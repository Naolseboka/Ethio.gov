package et.gov.ethgov.revenue.application.assessment;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentId;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAssessmentUseCase {
    private final AssessmentRepository repository;
    private final Clock clock;

    public ActivateAssessmentUseCase(AssessmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssessmentId execute(ActivateAssessmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AssessmentId id = AssessmentId.parse(command.assessmentId());
        Assessment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assessment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAssessmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assessmentId,
            String reason) {}
}
