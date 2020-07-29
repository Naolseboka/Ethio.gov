package et.gov.ethgov.document.application.classification;

import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationId;
import et.gov.ethgov.document.domain.classification.ClassificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateClassificationUseCase {
    private final ClassificationRepository repository;
    private final Clock clock;

    public UpdateClassificationUseCase(ClassificationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ClassificationId execute(UpdateClassificationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ClassificationId id = ClassificationId.parse(command.classificationId());
        Classification aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("classification not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateClassificationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String classificationId,
            String reason) {}
}
