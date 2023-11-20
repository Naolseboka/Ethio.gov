package et.gov.ethgov.payments.application;

import et.gov.ethgov.payments.domain.payment.PaymentDomainEvent;

public interface PaymentsEventPublisher {
    void publishAll(Iterable<PaymentDomainEvent> events);
}
