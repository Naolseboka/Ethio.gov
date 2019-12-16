package et.gov.ethgov.developer.domain.credential;

import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record ApiCredentialIssued(
        ApiCredentialId apiCredentialId,
        TenantId tenantId,
        CredentialNumber credentialNumber,
        DeveloperAppId developerAppId,
        String keyPrefix,
        Instant occurredAt)
        implements ApiCredentialDomainEvent {}
