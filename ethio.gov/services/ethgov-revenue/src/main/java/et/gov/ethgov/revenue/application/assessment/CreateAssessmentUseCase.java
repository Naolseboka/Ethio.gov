package et.gov.ethgov.revenue.application.assessment;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentId;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAssessmentUseCase {
    private final AssessmentRepository repository;
    private final Clock clock;

    public CreateAssessmentUseCase(AssessmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AssessmentId execute(CreateAssessmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Assessment aggregate = Assessment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAssessmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String assessmentId,
            String reason) {}
}
