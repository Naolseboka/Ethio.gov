package et.gov.ethgov.gateway.application;

import et.gov.ethgov.gateway.domain.audit.AuditEntryDomainEvent;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyDomainEvent;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyDomainEvent;

public interface GatewayEventPublisher {
    void publishRateLimitEvents(Iterable<RateLimitPolicyDomainEvent> events);

    void publishResilienceEvents(Iterable<ResiliencePolicyDomainEvent> events);

    void publishAuditEvents(Iterable<AuditEntryDomainEvent> events);
}
