package et.gov.ethgov.identity.domain.account;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record UserMembershipChanged(UserAccountId accountId, TenantId tenantId, Instant occurredAt)
        implements IdentityDomainEvent {}
