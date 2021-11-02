package et.gov.ethgov.identity.domain.client;

import java.util.Optional;

public interface OAuthClientRepository {

    boolean existsByClientId(String clientId);

    Optional<OAuthClient> findByClientId(String clientId);

    void save(OAuthClient client);
}
