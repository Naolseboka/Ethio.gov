package et.gov.ethgov.developer.domain.credential;

import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Optional;

public interface ApiCredentialRepository {
    Optional<ApiCredential> findById(ApiCredentialId id);

    Optional<ApiCredential> findByTenantAndCredentialNumber(
            TenantId tenantId, CredentialNumber credentialNumber);

    Optional<ApiCredential> findByKeyHash(String keyHash);

    List<ApiCredential> findByAppId(DeveloperAppId appId);

    void save(ApiCredential credential);
}
