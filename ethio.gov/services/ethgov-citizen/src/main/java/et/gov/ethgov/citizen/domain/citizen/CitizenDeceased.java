package et.gov.ethgov.citizen.domain.citizen;

import java.time.Instant;
import java.time.LocalDate;

public record CitizenDeceased(CitizenId citizenId, LocalDate deceasedOn, Instant occurredAt)
        implements CitizenDomainEvent {}
