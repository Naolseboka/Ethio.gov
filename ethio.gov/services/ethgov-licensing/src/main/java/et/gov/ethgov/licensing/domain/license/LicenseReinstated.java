package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseReinstated(LicenseId licenseId, Instant occurredAt) implements LicenseDomainEvent {}
