package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseClosed(CaseId caseId, Instant occurredAt) implements CaseDomainEvent {}
