package et.gov.ethgov.developer.application.quota;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import java.util.List;
import java.util.Objects;

public final class ListQuotaUseCase {
    private final QuotaRepository repository;

    public ListQuotaUseCase(QuotaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Quota> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
