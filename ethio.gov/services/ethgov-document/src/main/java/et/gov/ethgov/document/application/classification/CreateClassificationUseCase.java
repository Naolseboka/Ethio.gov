package et.gov.ethgov.document.application.classification;

import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationId;
import et.gov.ethgov.document.domain.classification.ClassificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateClassificationUseCase {
    private final ClassificationRepository repository;
    private final Clock clock;

    public CreateClassificationUseCase(ClassificationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ClassificationId execute(CreateClassificationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Classification aggregate = Classification.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateClassificationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String classificationId,
            String reason) {}
}
