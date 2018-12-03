package et.gov.ethgov.analytics.domain.observation;

import java.time.Instant;

public sealed interface ObservationDomainEvent permits ObservationRecorded, ObservationVoided {
    ObservationId observationId();

    Instant occurredAt();
}
