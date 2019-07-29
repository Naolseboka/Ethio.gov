package et.gov.ethgov.citizen.domain.citizen;

import java.time.Instant;

public sealed interface CitizenDomainEvent
        permits CitizenRegistered,
                CitizenDemographicsUpdated,
                CitizenFaydaAssigned,
                CitizenIdentityLinked,
                CitizenDeceased {

    CitizenId citizenId();

    Instant occurredAt();
}
