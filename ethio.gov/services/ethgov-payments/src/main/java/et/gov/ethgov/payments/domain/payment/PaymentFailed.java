package et.gov.ethgov.payments.domain.payment;
import java.time.Instant;
public record PaymentFailed(PaymentId paymentId, String reason, Instant occurredAt) implements PaymentDomainEvent {}
