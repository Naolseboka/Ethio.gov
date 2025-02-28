package et.gov.ethgov.revenue.domain.bill;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record BillCreated(
        BillId billId, TenantId tenantId, BillNumber billNumber, RevenueType type, Instant occurredAt)
        implements BillDomainEvent {}
