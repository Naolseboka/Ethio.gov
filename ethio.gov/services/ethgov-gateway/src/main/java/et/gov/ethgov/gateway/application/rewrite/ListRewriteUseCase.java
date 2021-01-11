package et.gov.ethgov.gateway.application.rewrite;

import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteRepository;
import java.util.List;
import java.util.Objects;

public final class ListRewriteUseCase {
    private final RewriteRepository repository;

    public ListRewriteUseCase(RewriteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Rewrite> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
