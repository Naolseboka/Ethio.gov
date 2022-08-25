package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseDocumentAttached(LicenseId licenseId, String documentId, Instant occurredAt)
        implements LicenseDomainEvent {}
