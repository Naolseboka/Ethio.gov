package et.gov.ethgov.licensing.domain.renewal;

import java.util.List;
import java.util.Optional;

public interface RenewalRepository {
    Renewal save(Renewal aggregate);

    Optional<Renewal> findById(RenewalId id);

    Optional<Renewal> findByReferenceCode(String tenantId, String referenceCode);

    List<Renewal> findByTenant(String tenantId, int limit);

    void delete(RenewalId id);
}
