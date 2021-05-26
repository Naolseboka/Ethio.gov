package et.gov.ethgov.gis.application.layer;

import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerId;
import et.gov.ethgov.gis.domain.layer.LayerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLayerUseCase {
    private final LayerRepository repository;
    private final Clock clock;

    public CreateLayerUseCase(LayerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LayerId execute(CreateLayerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Layer aggregate = Layer.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLayerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String layerId,
            String reason) {}
}
