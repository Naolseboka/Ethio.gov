package et.gov.ethgov.permits.domain.permit;

import java.time.Instant;

public sealed interface PermitDomainEvent
        permits PermitApplied, PermitReviewStarted, PermitIssued, PermitActivated,
                PermitSuspended, PermitReinstated, PermitCompleted, PermitRevoked,
                PermitRejected, PermitExpired, PermitDocumentAttached, PermitCancelled,
                PermitUpdated {
    PermitId permitId();
    Instant occurredAt();
}
