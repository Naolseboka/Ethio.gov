package et.gov.ethgov.permits.domain.permit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record PermitApplied(
        PermitId permitId, TenantId tenantId, PermitNumber permitNumber, PermitType type, Instant occurredAt)
        implements PermitDomainEvent {}
