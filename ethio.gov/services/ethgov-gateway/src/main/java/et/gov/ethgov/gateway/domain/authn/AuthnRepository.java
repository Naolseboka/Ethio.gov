package et.gov.ethgov.gateway.domain.authn;

import java.util.List;
import java.util.Optional;

public interface AuthnRepository {
    Authn save(Authn aggregate);

    Optional<Authn> findById(AuthnId id);

    Optional<Authn> findByReferenceCode(String tenantId, String referenceCode);

    List<Authn> findByTenant(String tenantId, int limit);

    void delete(AuthnId id);
}
