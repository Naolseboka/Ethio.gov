package et.gov.ethgov.gateway.application.healthcheck;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import java.util.List;
import java.util.Objects;

public final class ListHealthcheckUseCase {
    private final HealthcheckRepository repository;

    public ListHealthcheckUseCase(HealthcheckRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Healthcheck> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
