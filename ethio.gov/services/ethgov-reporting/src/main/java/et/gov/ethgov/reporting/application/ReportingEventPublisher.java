package et.gov.ethgov.reporting.application;

import et.gov.ethgov.reporting.domain.definition.ReportDefinitionDomainEvent;
import et.gov.ethgov.reporting.domain.run.ReportRunDomainEvent;

public interface ReportingEventPublisher {
    void publishDefinitionEvents(Iterable<ReportDefinitionDomainEvent> events);

    void publishRunEvents(Iterable<ReportRunDomainEvent> events);
}
