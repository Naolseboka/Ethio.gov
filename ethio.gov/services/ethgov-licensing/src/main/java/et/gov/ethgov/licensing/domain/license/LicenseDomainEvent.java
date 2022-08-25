package et.gov.ethgov.licensing.domain.license;

import java.time.Instant;

public sealed interface LicenseDomainEvent
        permits LicenseApplied, LicenseReviewStarted, LicenseIssued, LicenseActivated,
                LicenseSuspended, LicenseReinstated, LicenseRevoked, LicenseRejected,
                LicenseRenewed, LicenseExpired, LicenseDocumentAttached, LicenseCancelled,
                LicenseUpdated {
    LicenseId licenseId();
    Instant occurredAt();
}
