package et.gov.ethgov.developer.application.usage;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import java.util.List;
import java.util.Objects;

public final class ListUsageUseCase {
    private final UsageRepository repository;

    public ListUsageUseCase(UsageRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Usage> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
