package et.gov.ethgov.permits.domain.permitinspection;

import java.util.List;
import java.util.Optional;

public interface PermitinspectionRepository {
    Permitinspection save(Permitinspection aggregate);

    Optional<Permitinspection> findById(PermitinspectionId id);

    Optional<Permitinspection> findByReferenceCode(String tenantId, String referenceCode);

    List<Permitinspection> findByTenant(String tenantId, int limit);

    void delete(PermitinspectionId id);
}
