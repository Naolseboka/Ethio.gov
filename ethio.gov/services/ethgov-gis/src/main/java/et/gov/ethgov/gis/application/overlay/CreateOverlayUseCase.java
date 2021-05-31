package et.gov.ethgov.gis.application.overlay;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayId;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOverlayUseCase {
    private final OverlayRepository repository;
    private final Clock clock;

    public CreateOverlayUseCase(OverlayRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OverlayId execute(CreateOverlayCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Overlay aggregate = Overlay.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOverlayCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String overlayId,
            String reason) {}
}
