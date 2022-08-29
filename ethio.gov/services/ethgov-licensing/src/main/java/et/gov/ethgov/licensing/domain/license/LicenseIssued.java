package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseIssued(LicenseId licenseId, Instant occurredAt) implements LicenseDomainEvent {}
