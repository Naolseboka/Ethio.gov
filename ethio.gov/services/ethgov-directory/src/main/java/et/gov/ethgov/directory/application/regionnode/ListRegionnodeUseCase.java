package et.gov.ethgov.directory.application.regionnode;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import java.util.List;
import java.util.Objects;

public final class ListRegionnodeUseCase {
    private final RegionnodeRepository repository;

    public ListRegionnodeUseCase(RegionnodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Regionnode> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
