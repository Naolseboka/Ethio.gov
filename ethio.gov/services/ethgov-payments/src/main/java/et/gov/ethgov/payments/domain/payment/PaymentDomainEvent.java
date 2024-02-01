package et.gov.ethgov.payments.domain.payment;

import java.time.Instant;

public sealed interface PaymentDomainEvent
        permits PaymentInitiated, PaymentSucceeded, PaymentFailed, PaymentCancelled, PaymentRefunded {
    PaymentId paymentId();
    Instant occurredAt();
}
