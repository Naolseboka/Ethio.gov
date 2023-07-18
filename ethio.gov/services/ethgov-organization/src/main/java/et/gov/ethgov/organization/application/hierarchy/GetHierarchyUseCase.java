package et.gov.ethgov.organization.application.hierarchy;

import et.gov.ethgov.organization.domain.hierarchy.Hierarchy;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyId;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyRepository;
import java.util.Objects;

public final class GetHierarchyUseCase {
    private final HierarchyRepository repository;

    public GetHierarchyUseCase(HierarchyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Hierarchy execute(String rawId) {
        HierarchyId id = HierarchyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hierarchy not found: " + rawId));
    }
}
