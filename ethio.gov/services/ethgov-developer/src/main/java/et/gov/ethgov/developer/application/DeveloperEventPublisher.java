package et.gov.ethgov.developer.application;

import et.gov.ethgov.developer.domain.app.DeveloperAppDomainEvent;
import et.gov.ethgov.developer.domain.credential.ApiCredentialDomainEvent;

public interface DeveloperEventPublisher {
    void publishAppEvents(Iterable<DeveloperAppDomainEvent> events);

    void publishCredentialEvents(Iterable<ApiCredentialDomainEvent> events);
}
