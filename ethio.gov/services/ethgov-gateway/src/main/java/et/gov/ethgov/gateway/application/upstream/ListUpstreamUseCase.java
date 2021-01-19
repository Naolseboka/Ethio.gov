package et.gov.ethgov.gateway.application.upstream;

import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamRepository;
import java.util.List;
import java.util.Objects;

public final class ListUpstreamUseCase {
    private final UpstreamRepository repository;

    public ListUpstreamUseCase(UpstreamRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Upstream> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
