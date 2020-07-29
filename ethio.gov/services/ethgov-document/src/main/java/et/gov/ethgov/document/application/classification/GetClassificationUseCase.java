package et.gov.ethgov.document.application.classification;

import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationId;
import et.gov.ethgov.document.domain.classification.ClassificationRepository;
import java.util.Objects;

public final class GetClassificationUseCase {
    private final ClassificationRepository repository;

    public GetClassificationUseCase(ClassificationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Classification execute(String rawId) {
        ClassificationId id = ClassificationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("classification not found: " + rawId));
    }
}
