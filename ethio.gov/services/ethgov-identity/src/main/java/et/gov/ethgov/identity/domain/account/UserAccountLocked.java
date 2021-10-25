package et.gov.ethgov.identity.domain.account;

import java.time.Instant;

public record UserAccountLocked(UserAccountId accountId, Instant lockedUntil, Instant occurredAt)
        implements IdentityDomainEvent {}
