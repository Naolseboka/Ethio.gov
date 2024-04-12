package et.gov.ethgov.permits.application.extension;

import et.gov.ethgov.permits.domain.extension.Extension;
import et.gov.ethgov.permits.domain.extension.ExtensionId;
import et.gov.ethgov.permits.domain.extension.ExtensionRepository;
import java.util.Objects;

public final class GetExtensionUseCase {
    private final ExtensionRepository repository;

    public GetExtensionUseCase(ExtensionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Extension execute(String rawId) {
        ExtensionId id = ExtensionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("extension not found: " + rawId));
    }
}
