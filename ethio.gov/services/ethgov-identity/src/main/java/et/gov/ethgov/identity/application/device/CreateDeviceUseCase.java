package et.gov.ethgov.identity.application.device;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceId;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDeviceUseCase {
    private final DeviceRepository repository;
    private final Clock clock;

    public CreateDeviceUseCase(DeviceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DeviceId execute(CreateDeviceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Device aggregate = Device.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDeviceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String deviceId,
            String reason) {}
}
