package et.gov.ethgov.licensing.domain.license;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record LicenseApplied(
        LicenseId licenseId, TenantId tenantId, LicenseNumber licenseNumber, LicenseType type, Instant occurredAt)
        implements LicenseDomainEvent {}
