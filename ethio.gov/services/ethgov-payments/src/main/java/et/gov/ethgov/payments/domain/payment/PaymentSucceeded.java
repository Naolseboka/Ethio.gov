package et.gov.ethgov.payments.domain.payment;
import java.time.Instant;
public record PaymentSucceeded(PaymentId paymentId, String providerReference, Instant occurredAt)
        implements PaymentDomainEvent {}
