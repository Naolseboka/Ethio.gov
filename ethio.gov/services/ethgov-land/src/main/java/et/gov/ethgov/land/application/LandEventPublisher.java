package et.gov.ethgov.land.application;

import et.gov.ethgov.land.domain.parcel.ParcelDomainEvent;

public interface LandEventPublisher {
    void publishAll(Iterable<ParcelDomainEvent> events);
}
