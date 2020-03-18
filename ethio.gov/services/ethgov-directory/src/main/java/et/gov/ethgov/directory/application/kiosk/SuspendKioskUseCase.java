package et.gov.ethgov.directory.application.kiosk;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskId;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendKioskUseCase {
    private final KioskRepository repository;
    private final Clock clock;

    public SuspendKioskUseCase(KioskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KioskId execute(SuspendKioskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        KioskId id = KioskId.parse(command.kioskId());
        Kiosk aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("kiosk not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendKioskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String kioskId,
            String reason) {}
}
