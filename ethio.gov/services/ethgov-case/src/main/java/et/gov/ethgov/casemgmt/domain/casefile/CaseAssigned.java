package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseAssigned(CaseId caseId, String assigneeAccountId, Instant occurredAt)
        implements CaseDomainEvent {}
