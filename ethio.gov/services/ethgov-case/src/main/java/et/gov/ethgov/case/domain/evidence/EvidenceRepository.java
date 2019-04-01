package et.gov.ethgov.case.domain.evidence;

import java.util.List;
import java.util.Optional;

public interface EvidenceRepository {
    Evidence save(Evidence aggregate);

    Optional<Evidence> findById(EvidenceId id);

    Optional<Evidence> findByReferenceCode(String tenantId, String referenceCode);

    List<Evidence> findByTenant(String tenantId, int limit);

    void delete(EvidenceId id);
}
