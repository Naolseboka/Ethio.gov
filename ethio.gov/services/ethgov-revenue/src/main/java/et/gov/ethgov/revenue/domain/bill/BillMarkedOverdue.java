package et.gov.ethgov.revenue.domain.bill;
import java.time.Instant;
public record BillMarkedOverdue(BillId billId, Instant occurredAt) implements BillDomainEvent {}
