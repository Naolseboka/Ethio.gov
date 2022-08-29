package et.gov.ethgov.licensing.domain.license;
import java.time.Instant;
public record LicenseRenewed(LicenseId licenseId, Instant validTo, Instant occurredAt) implements LicenseDomainEvent {}
