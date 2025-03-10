package et.gov.ethgov.revenue.domain.bill;
import java.time.Instant;
public record BillWrittenOff(BillId billId, String reason, Instant occurredAt) implements BillDomainEvent {}
