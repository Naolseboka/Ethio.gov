package et.gov.ethgov.directory.application.kiosk;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskId;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateKioskUseCase {
    private final KioskRepository repository;
    private final Clock clock;

    public CreateKioskUseCase(KioskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KioskId execute(CreateKioskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Kiosk aggregate = Kiosk.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateKioskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String kioskId,
            String reason) {}
}
