package et.gov.ethgov.gateway.domain.audit;

import java.time.Instant;

public sealed interface AuditEntryDomainEvent permits AuditEntryAppended {
    AuditEntryId auditEntryId();
    Instant occurredAt();
}
