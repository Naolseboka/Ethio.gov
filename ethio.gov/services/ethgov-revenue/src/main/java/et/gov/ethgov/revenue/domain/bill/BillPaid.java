package et.gov.ethgov.revenue.domain.bill;
import java.time.Instant;
public record BillPaid(BillId billId, String paymentReference, Instant occurredAt) implements BillDomainEvent {}
