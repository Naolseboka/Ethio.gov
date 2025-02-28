package et.gov.ethgov.revenue.domain.bill;

import java.time.Instant;

public sealed interface BillDomainEvent
        permits BillCreated, BillUpdated, BillIssued, BillMarkedOverdue, BillPaid,
                BillCancelled, BillWrittenOff {
    BillId billId();
    Instant occurredAt();
}
