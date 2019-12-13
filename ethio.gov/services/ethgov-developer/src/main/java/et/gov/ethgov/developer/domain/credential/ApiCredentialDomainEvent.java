package et.gov.ethgov.developer.domain.credential;

import java.time.Instant;

public sealed interface ApiCredentialDomainEvent permits ApiCredentialIssued, ApiCredentialRevoked {
    ApiCredentialId apiCredentialId();

    Instant occurredAt();
}
