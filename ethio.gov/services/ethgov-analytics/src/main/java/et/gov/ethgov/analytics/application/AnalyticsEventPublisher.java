package et.gov.ethgov.analytics.application;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionDomainEvent;
import et.gov.ethgov.analytics.domain.observation.ObservationDomainEvent;

public interface AnalyticsEventPublisher {
    void publishMetricEvents(Iterable<MetricDefinitionDomainEvent> events);

    void publishObservationEvents(Iterable<ObservationDomainEvent> events);
}
