package et.gov.ethgov.opendata.application;

import et.gov.ethgov.opendata.domain.dataset.DatasetDomainEvent;
import et.gov.ethgov.opendata.domain.resource.ResourceDomainEvent;

public interface OpenDataEventPublisher {
    void publishDatasetEvents(Iterable<DatasetDomainEvent> events);

    void publishResourceEvents(Iterable<ResourceDomainEvent> events);
}
