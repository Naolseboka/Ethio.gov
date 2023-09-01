package et.gov.ethgov.organization.domain.hierarchy;

import java.util.List;
import java.util.Optional;

public interface HierarchyRepository {
    Hierarchy save(Hierarchy aggregate);

    Optional<Hierarchy> findById(HierarchyId id);

    Optional<Hierarchy> findByReferenceCode(String tenantId, String referenceCode);

    List<Hierarchy> findByTenant(String tenantId, int limit);

    void delete(HierarchyId id);
}
