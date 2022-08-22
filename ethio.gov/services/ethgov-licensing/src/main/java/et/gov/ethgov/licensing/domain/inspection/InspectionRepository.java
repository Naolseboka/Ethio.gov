package et.gov.ethgov.licensing.domain.inspection;

import java.util.List;
import java.util.Optional;

public interface InspectionRepository {
    Inspection save(Inspection aggregate);

    Optional<Inspection> findById(InspectionId id);

    Optional<Inspection> findByReferenceCode(String tenantId, String referenceCode);

    List<Inspection> findByTenant(String tenantId, int limit);

    void delete(InspectionId id);
}
