package et.gov.ethgov.licensing.domain.compliance;

import java.util.List;
import java.util.Optional;

public interface ComplianceRepository {
    Compliance save(Compliance aggregate);

    Optional<Compliance> findById(ComplianceId id);

    Optional<Compliance> findByReferenceCode(String tenantId, String referenceCode);

    List<Compliance> findByTenant(String tenantId, int limit);

    void delete(ComplianceId id);
}
