package et.gov.ethgov.identity.domain.oauthclient;

import java.util.List;
import java.util.Optional;

public interface OauthclientRepository {
    Oauthclient save(Oauthclient aggregate);

    Optional<Oauthclient> findById(OauthclientId id);

    Optional<Oauthclient> findByReferenceCode(String tenantId, String referenceCode);

    List<Oauthclient> findByTenant(String tenantId, int limit);

    void delete(OauthclientId id);
}
