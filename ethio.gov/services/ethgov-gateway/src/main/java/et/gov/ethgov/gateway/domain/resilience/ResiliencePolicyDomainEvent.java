package et.gov.ethgov.gateway.domain.resilience;

import java.time.Instant;

public sealed interface ResiliencePolicyDomainEvent
        permits ResiliencePolicyCreated,
                ResiliencePolicyActivated,
                ResiliencePolicySuspended,
                ResiliencePolicyRetired,
                ResiliencePolicyCancelled,
                ResiliencePolicyUpdated {
    ResiliencePolicyId resiliencePolicyId();
    Instant occurredAt();
}
