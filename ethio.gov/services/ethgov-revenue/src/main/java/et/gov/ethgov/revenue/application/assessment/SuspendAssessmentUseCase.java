package et.gov.ethgov.revenue.application.assessment;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentId;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendAssessmentUseCase {
    private final AssessmentRepository repository;
    private final Clock clock;

    public SuspendAssessmentUseCase(AssessmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssessmentId execute(SuspendAssessmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AssessmentId id = AssessmentId.parse(command.assessmentId());
        Assessment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("assessment not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendAssessmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assessmentId,
            String reason) {}
}
