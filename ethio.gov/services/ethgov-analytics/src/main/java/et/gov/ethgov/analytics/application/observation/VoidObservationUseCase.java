package et.gov.ethgov.analytics.application.observation;

import et.gov.ethgov.analytics.application.AnalyticsEventPublisher;
import et.gov.ethgov.analytics.domain.observation.MetricObservation;
import et.gov.ethgov.analytics.domain.observation.MetricObservationRepository;
import et.gov.ethgov.analytics.domain.observation.ObservationId;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoidObservationUseCase {

    private final MetricObservationRepository observations;
    private final AnalyticsEventPublisher eventPublisher;
    private final Clock clock;

    public VoidObservationUseCase(
            MetricObservationRepository observations, AnalyticsEventPublisher eventPublisher, Clock clock) {
        this.observations = observations;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String observationId, String reason) {
        Objects.requireNonNull(observationId, "observationId");
        MetricObservation observation = observations
                .findById(ObservationId.of(observationId))
                .orElseThrow(() -> new DomainException(
                        "OBSERVATION_NOT_FOUND",
                        "Observation Not Found",
                        "Metric observation does not exist",
                        404));
        observation.voidObservation(reason, clock.instant());
        observations.save(observation);
        eventPublisher.publishObservationEvents(observation.pullDomainEvents());
    }
}
