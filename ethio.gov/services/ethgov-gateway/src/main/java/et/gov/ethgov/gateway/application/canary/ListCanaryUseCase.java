package et.gov.ethgov.gateway.application.canary;

import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryRepository;
import java.util.List;
import java.util.Objects;

public final class ListCanaryUseCase {
    private final CanaryRepository repository;

    public ListCanaryUseCase(CanaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Canary> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
