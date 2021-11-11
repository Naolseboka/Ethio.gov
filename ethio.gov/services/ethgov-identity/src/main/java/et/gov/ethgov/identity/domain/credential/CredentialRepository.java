package et.gov.ethgov.identity.domain.credential;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository {
    Credential save(Credential aggregate);

    Optional<Credential> findById(CredentialId id);

    Optional<Credential> findByReferenceCode(String tenantId, String referenceCode);

    List<Credential> findByTenant(String tenantId, int limit);

    void delete(CredentialId id);
}
