package et.gov.ethgov.organization.application.hierarchy;

import et.gov.ethgov.organization.domain.hierarchy.Hierarchy;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyRepository;
import java.util.List;
import java.util.Objects;

public final class ListHierarchyUseCase {
    private final HierarchyRepository repository;

    public ListHierarchyUseCase(HierarchyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Hierarchy> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
