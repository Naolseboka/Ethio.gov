package et.gov.ethgov.gateway.domain.resilience;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record ResiliencePolicyCreated(
        ResiliencePolicyId resiliencePolicyId,
        TenantId tenantId,
        ResiliencePolicyNumber policyNumber,
        String dependencyName,
        Instant occurredAt) implements ResiliencePolicyDomainEvent {}
