package et.gov.ethgov.developer.domain.credential;

import java.time.Instant;

public record ApiCredentialRevoked(ApiCredentialId apiCredentialId, String reason, Instant occurredAt)
        implements ApiCredentialDomainEvent {}
