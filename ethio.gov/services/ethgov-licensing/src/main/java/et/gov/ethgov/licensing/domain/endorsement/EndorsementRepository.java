package et.gov.ethgov.licensing.domain.endorsement;

import java.util.List;
import java.util.Optional;

public interface EndorsementRepository {
    Endorsement save(Endorsement aggregate);

    Optional<Endorsement> findById(EndorsementId id);

    Optional<Endorsement> findByReferenceCode(String tenantId, String referenceCode);

    List<Endorsement> findByTenant(String tenantId, int limit);

    void delete(EndorsementId id);
}
