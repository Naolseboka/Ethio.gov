package et.gov.ethgov.revenue.application;

import et.gov.ethgov.revenue.domain.bill.BillDomainEvent;

public interface RevenueEventPublisher {
    void publishAll(Iterable<BillDomainEvent> events);
}
