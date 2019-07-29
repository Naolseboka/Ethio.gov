package et.gov.ethgov.citizen.domain.citizen;

import java.time.Instant;

public record CitizenDemographicsUpdated(CitizenId citizenId, Instant occurredAt)
        implements CitizenDomainEvent {}
