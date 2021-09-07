package et.gov.ethgov.identity.application.account;

import et.gov.ethgov.identity.domain.account.IdentityDomainEvent;

public interface IdentityEventPublisher {

    void publishAll(Iterable<IdentityDomainEvent> events);
}
