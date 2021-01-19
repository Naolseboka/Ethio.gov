package et.gov.ethgov.gateway.domain.audit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record AuditEntryAppended(
        AuditEntryId auditEntryId,
        TenantId tenantId,
        String action,
        String resourceType,
        String resourceId,
        AuditOutcome outcome,
        Instant occurredAt) implements AuditEntryDomainEvent {}
