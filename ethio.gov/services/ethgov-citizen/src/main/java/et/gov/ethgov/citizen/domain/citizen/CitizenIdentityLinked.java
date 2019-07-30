package et.gov.ethgov.citizen.domain.citizen;

import java.time.Instant;

public record CitizenIdentityLinked(CitizenId citizenId, String identityAccountId, Instant occurredAt)
        implements CitizenDomainEvent {}
