package et.gov.ethgov.citizen.domain.citizen;

import java.time.Instant;

public record CitizenFaydaAssigned(CitizenId citizenId, FaydaId faydaId, Instant occurredAt)
        implements CitizenDomainEvent {}
