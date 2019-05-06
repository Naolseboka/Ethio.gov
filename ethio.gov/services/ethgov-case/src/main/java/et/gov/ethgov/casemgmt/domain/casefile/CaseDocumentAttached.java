package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseDocumentAttached(CaseId caseId, String documentId, Instant occurredAt)
        implements CaseDomainEvent {}
