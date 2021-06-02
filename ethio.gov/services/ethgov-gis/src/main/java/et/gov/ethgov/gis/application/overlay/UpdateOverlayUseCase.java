package et.gov.ethgov.gis.application.overlay;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayId;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateOverlayUseCase {
    private final OverlayRepository repository;
    private final Clock clock;

    public UpdateOverlayUseCase(OverlayRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OverlayId execute(UpdateOverlayCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OverlayId id = OverlayId.parse(command.overlayId());
        Overlay aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("overlay not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateOverlayCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String overlayId,
            String reason) {}
}
