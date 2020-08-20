package et.gov.ethgov.document.application.scanbatch;

import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchId;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchRepository;
import java.util.Objects;

public final class GetScanbatchUseCase {
    private final ScanbatchRepository repository;

    public GetScanbatchUseCase(ScanbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Scanbatch execute(String rawId) {
        ScanbatchId id = ScanbatchId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scanbatch not found: " + rawId));
    }
}
