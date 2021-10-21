package et.gov.ethgov.identity.domain.account;

import java.time.Instant;

public sealed interface IdentityDomainEvent
        permits UserAccountRegistered, UserAccountDisabled, UserAccountLocked, UserMembershipChanged {

    UserAccountId accountId();

    Instant occurredAt();
}
