package et.gov.ethgov.revenue.domain.bill;
import java.time.Instant;
public record BillIssued(BillId billId, Instant dueAt, Instant occurredAt) implements BillDomainEvent {}
