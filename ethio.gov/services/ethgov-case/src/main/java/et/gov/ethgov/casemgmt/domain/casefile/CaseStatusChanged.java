package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseStatusChanged(
        CaseId caseId, CaseStatus from, CaseStatus to, Instant occurredAt)
        implements CaseDomainEvent {}
