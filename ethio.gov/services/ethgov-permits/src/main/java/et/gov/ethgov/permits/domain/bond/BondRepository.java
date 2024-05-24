package et.gov.ethgov.permits.domain.bond;

import java.util.List;
import java.util.Optional;

public interface BondRepository {
    Bond save(Bond aggregate);

    Optional<Bond> findById(BondId id);

    Optional<Bond> findByReferenceCode(String tenantId, String referenceCode);

    List<Bond> findByTenant(String tenantId, int limit);

    void delete(BondId id);
}
