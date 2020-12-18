package et.gov.ethgov.gateway.application.cors;

import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsId;
import et.gov.ethgov.gateway.domain.cors.CorsRepository;
import java.util.Objects;

public final class GetCorsUseCase {
    private final CorsRepository repository;

    public GetCorsUseCase(CorsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Cors execute(String rawId) {
        CorsId id = CorsId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cors not found: " + rawId));
    }
}
