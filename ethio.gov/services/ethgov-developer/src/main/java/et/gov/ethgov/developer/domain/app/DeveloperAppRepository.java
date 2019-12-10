package et.gov.ethgov.developer.domain.app;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface DeveloperAppRepository {
    boolean existsByTenantAndSlug(TenantId tenantId, AppSlug slug);

    Optional<DeveloperApp> findById(DeveloperAppId id);

    Optional<DeveloperApp> findByTenantAndAppNumber(TenantId tenantId, AppNumber appNumber);

    Optional<DeveloperApp> findByTenantAndSlug(TenantId tenantId, AppSlug slug);

    void save(DeveloperApp app);
}
