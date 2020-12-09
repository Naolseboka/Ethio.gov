package et.gov.ethgov.gateway.application.canary;

import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryId;
import et.gov.ethgov.gateway.domain.canary.CanaryRepository;
import java.util.Objects;

public final class GetCanaryUseCase {
    private final CanaryRepository repository;

    public GetCanaryUseCase(CanaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Canary execute(String rawId) {
        CanaryId id = CanaryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("canary not found: " + rawId));
    }
}
