package et.gov.ethgov.casemgmt.domain.casefile;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record CaseOpened(
        CaseId caseId, TenantId tenantId, CaseNumber caseNumber, CaseType type, Instant occurredAt)
        implements CaseDomainEvent {}
