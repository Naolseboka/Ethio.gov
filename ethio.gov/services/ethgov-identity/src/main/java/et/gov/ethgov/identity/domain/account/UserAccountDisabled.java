package et.gov.ethgov.identity.domain.account;

import java.time.Instant;

public record UserAccountDisabled(UserAccountId accountId, Instant occurredAt)
        implements IdentityDomainEvent {}
