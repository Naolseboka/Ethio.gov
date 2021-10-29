package et.gov.ethgov.identity.domain.apikey;

import java.util.List;
import java.util.Optional;

public interface ApikeyRepository {
    Apikey save(Apikey aggregate);

    Optional<Apikey> findById(ApikeyId id);

    Optional<Apikey> findByReferenceCode(String tenantId, String referenceCode);

    List<Apikey> findByTenant(String tenantId, int limit);

    void delete(ApikeyId id);
}
