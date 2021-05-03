package et.gov.ethgov.gis.application;

import et.gov.ethgov.gis.domain.feature.FeatureDomainEvent;

public interface GisEventPublisher {
    void publishAll(Iterable<FeatureDomainEvent> events);
}
