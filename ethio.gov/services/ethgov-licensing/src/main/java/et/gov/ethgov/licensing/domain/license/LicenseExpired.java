package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseExpired(LicenseId licenseId, Instant occurredAt) implements LicenseDomainEvent {}
