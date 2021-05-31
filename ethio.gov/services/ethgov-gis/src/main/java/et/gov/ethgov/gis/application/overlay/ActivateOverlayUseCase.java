package et.gov.ethgov.gis.application.overlay;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayId;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateOverlayUseCase {
    private final OverlayRepository repository;
    private final Clock clock;

    public ActivateOverlayUseCase(OverlayRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OverlayId execute(ActivateOverlayCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OverlayId id = OverlayId.parse(command.overlayId());
        Overlay aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("overlay not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateOverlayCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String overlayId,
            String reason) {}
}
