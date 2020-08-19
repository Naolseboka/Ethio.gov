package et.gov.ethgov.document.application.retention;

import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionId;
import et.gov.ethgov.document.domain.retention.RetentionRepository;
import java.util.Objects;

public final class GetRetentionUseCase {
    private final RetentionRepository repository;

    public GetRetentionUseCase(RetentionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Retention execute(String rawId) {
        RetentionId id = RetentionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("retention not found: " + rawId));
    }
}
