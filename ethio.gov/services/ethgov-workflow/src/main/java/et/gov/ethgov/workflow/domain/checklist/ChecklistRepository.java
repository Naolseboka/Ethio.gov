package et.gov.ethgov.workflow.domain.checklist;

import java.util.List;
import java.util.Optional;

public interface ChecklistRepository {
    Checklist save(Checklist aggregate);

    Optional<Checklist> findById(ChecklistId id);

    Optional<Checklist> findByReferenceCode(String tenantId, String referenceCode);

    List<Checklist> findByTenant(String tenantId, int limit);

    void delete(ChecklistId id);
}
