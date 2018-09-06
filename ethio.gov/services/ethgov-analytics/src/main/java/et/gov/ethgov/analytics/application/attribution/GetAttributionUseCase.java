package et.gov.ethgov.analytics.application.attribution;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionId;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import java.util.Objects;

public final class GetAttributionUseCase {
    private final AttributionRepository repository;

    public GetAttributionUseCase(AttributionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Attribution execute(String rawId) {
        AttributionId id = AttributionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("attribution not found: " + rawId));
    }
}
