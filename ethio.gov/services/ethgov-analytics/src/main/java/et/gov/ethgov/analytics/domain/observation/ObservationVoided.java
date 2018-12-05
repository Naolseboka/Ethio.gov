package et.gov.ethgov.analytics.domain.observation;

import java.time.Instant;

public record ObservationVoided(ObservationId observationId, String reason, Instant occurredAt)
        implements ObservationDomainEvent {}
