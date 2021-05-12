package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateFeatureUseCase {
    private final FeatureRepository repository;
    private final Clock clock;

    public CreateFeatureUseCase(FeatureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FeatureId execute(CreateFeatureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Feature aggregate = Feature.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateFeatureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String featureId,
            String reason) {}
}
