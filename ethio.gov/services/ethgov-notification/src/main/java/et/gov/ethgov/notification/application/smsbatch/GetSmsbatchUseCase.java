package et.gov.ethgov.notification.application.smsbatch;

import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchId;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchRepository;
import java.util.Objects;

public final class GetSmsbatchUseCase {
    private final SmsbatchRepository repository;

    public GetSmsbatchUseCase(SmsbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Smsbatch execute(String rawId) {
        SmsbatchId id = SmsbatchId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("smsbatch not found: " + rawId));
    }
}
