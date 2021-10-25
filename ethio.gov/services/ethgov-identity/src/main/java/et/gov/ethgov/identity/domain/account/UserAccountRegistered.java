package et.gov.ethgov.identity.domain.account;

import java.time.Instant;

public record UserAccountRegistered(
        UserAccountId accountId, Username username, AccountType accountType, Instant occurredAt)
        implements IdentityDomainEvent {}
