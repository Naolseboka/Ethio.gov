package et.gov.ethgov.payments.domain.payment;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record PaymentInitiated(
        PaymentId paymentId, TenantId tenantId, PaymentNumber paymentNumber, String billId, Instant occurredAt)
        implements PaymentDomainEvent {}
