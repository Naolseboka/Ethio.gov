package et.gov.ethgov.identity.domain.consent;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository {
    Consent save(Consent aggregate);

    Optional<Consent> findById(ConsentId id);

    Optional<Consent> findByReferenceCode(String tenantId, String referenceCode);

    List<Consent> findByTenant(String tenantId, int limit);

    void delete(ConsentId id);
}
