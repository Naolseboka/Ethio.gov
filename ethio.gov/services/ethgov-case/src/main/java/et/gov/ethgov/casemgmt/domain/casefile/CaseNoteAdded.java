package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseNoteAdded(CaseId caseId, String noteId, Instant occurredAt)
        implements CaseDomainEvent {}
