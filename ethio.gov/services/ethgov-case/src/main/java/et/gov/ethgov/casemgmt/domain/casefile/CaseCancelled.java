package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseCancelled(CaseId caseId, String reason, Instant occurredAt)
        implements CaseDomainEvent {}
