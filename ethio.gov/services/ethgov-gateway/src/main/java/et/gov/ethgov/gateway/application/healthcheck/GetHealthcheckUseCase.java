package et.gov.ethgov.gateway.application.healthcheck;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckId;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import java.util.Objects;

public final class GetHealthcheckUseCase {
    private final HealthcheckRepository repository;

    public GetHealthcheckUseCase(HealthcheckRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Healthcheck execute(String rawId) {
        HealthcheckId id = HealthcheckId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("healthcheck not found: " + rawId));
    }
}
