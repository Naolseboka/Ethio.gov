package et.gov.ethgov.gateway.domain.resilience;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface ResiliencePolicyRepository {
    boolean existsByTenantAndDependency(TenantId tenantId, String dependencyName);

    Optional<ResiliencePolicy> findById(ResiliencePolicyId id);

    Optional<ResiliencePolicy> findByTenantAndDependency(TenantId tenantId, String dependencyName);

    void save(ResiliencePolicy policy);
}
