package et.gov.ethgov.land.domain.lease;

import java.util.List;
import java.util.Optional;

public interface LeaseRepository {
    Lease save(Lease aggregate);

    Optional<Lease> findById(LeaseId id);

    Optional<Lease> findByReferenceCode(String tenantId, String referenceCode);

    List<Lease> findByTenant(String tenantId, int limit);

    void delete(LeaseId id);
}
