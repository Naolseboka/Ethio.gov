package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;

public record CaseWorkflowLinked(CaseId caseId, String processInstanceId, Instant occurredAt)
        implements CaseDomainEvent {}
