package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.util.List;
import java.util.Objects;

public final class ListFunnelUseCase {
    private final FunnelRepository repository;

    public ListFunnelUseCase(FunnelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Funnel> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
