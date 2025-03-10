package et.gov.ethgov.revenue.domain.bill;
import java.time.Instant;
public record BillUpdated(BillId billId, Instant occurredAt) implements BillDomainEvent {}
