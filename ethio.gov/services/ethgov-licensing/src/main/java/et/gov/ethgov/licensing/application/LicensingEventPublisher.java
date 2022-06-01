package et.gov.ethgov.licensing.application;

import et.gov.ethgov.licensing.domain.license.LicenseDomainEvent;

public interface LicensingEventPublisher {
    void publishAll(Iterable<LicenseDomainEvent> events);
}
