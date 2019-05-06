package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public sealed interface CaseDomainEvent
        permits CaseOpened,
                CaseUpdated,
                CaseAssigned,
                CaseStatusChanged,
                CaseWorkflowLinked,
                CaseDocumentAttached,
                CaseNoteAdded,
                CaseClosed,
                CaseCancelled {

    CaseId caseId();

    Instant occurredAt();
}
