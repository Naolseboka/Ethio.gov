package et.gov.ethgov.workflow.domain.sla;

import java.util.List;
import java.util.Optional;

public interface SlaRepository {
    Sla save(Sla aggregate);

    Optional<Sla> findById(SlaId id);

    Optional<Sla> findByReferenceCode(String tenantId, String referenceCode);

    List<Sla> findByTenant(String tenantId, int limit);

    void delete(SlaId id);
}
