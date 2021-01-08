package et.gov.ethgov.gateway.application.resilience;

import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicy;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyId;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetResiliencePolicyUseCase {
    private final ResiliencePolicyRepository policies;

    public GetResiliencePolicyUseCase(ResiliencePolicyRepository policies) {
        this.policies = policies;
    }

    @Transactional(readOnly = true)
    public ResiliencePolicy byId(String resiliencePolicyId) {
        return policies
                .findById(ResiliencePolicyId.of(resiliencePolicyId))
                .orElseThrow(() -> new DomainException(
                        "RESILIENCE_NOT_FOUND",
                        "Resilience Not Found",
                        "Unknown policy " + resiliencePolicyId,
                        404));
    }

    @Transactional(readOnly = true)
    public ResiliencePolicy byDependency(String tenantId, String dependencyName) {
        return policies
                .findByTenantAndDependency(
                        TenantId.of(tenantId), dependencyName.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new DomainException(
                        "RESILIENCE_NOT_FOUND",
                        "Resilience Not Found",
                        "Unknown dependency " + dependencyName,
                        404));
    }
}
