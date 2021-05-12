package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateFeatureUseCase {
    private final FeatureRepository repository;
    private final Clock clock;

    public ActivateFeatureUseCase(FeatureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FeatureId execute(ActivateFeatureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FeatureId id = FeatureId.parse(command.featureId());
        Feature aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("feature not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateFeatureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String featureId,
            String reason) {}
}
