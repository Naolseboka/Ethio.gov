package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseRejected(LicenseId licenseId, String reason, Instant occurredAt) implements LicenseDomainEvent {}
