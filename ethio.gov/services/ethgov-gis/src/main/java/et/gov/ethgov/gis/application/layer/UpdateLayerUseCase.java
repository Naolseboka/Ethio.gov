package et.gov.ethgov.gis.application.layer;

import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerId;
import et.gov.ethgov.gis.domain.layer.LayerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateLayerUseCase {
    private final LayerRepository repository;
    private final Clock clock;

    public UpdateLayerUseCase(LayerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LayerId execute(UpdateLayerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LayerId id = LayerId.parse(command.layerId());
        Layer aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("layer not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateLayerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String layerId,
            String reason) {}
}
