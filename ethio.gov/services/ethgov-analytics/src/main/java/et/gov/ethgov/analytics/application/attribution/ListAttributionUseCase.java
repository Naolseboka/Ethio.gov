package et.gov.ethgov.analytics.application.attribution;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import java.util.List;
import java.util.Objects;

public final class ListAttributionUseCase {
    private final AttributionRepository repository;

    public ListAttributionUseCase(AttributionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Attribution> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
