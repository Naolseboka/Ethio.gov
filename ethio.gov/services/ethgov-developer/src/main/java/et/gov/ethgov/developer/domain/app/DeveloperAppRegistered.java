package et.gov.ethgov.developer.domain.app;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record DeveloperAppRegistered(
        DeveloperAppId developerAppId,
        TenantId tenantId,
        AppNumber appNumber,
        AppSlug slug,
        Instant occurredAt)
        implements DeveloperAppDomainEvent {}
