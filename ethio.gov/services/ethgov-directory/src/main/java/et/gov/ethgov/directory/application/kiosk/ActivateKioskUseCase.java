package et.gov.ethgov.directory.application.kiosk;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskId;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateKioskUseCase {
    private final KioskRepository repository;
    private final Clock clock;

    public ActivateKioskUseCase(KioskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KioskId execute(ActivateKioskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        KioskId id = KioskId.parse(command.kioskId());
        Kiosk aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("kiosk not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateKioskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String kioskId,
            String reason) {}
}
