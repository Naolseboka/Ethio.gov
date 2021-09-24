package et.gov.ethgov.identity.application.device;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceId;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDeviceUseCase {
    private final DeviceRepository repository;
    private final Clock clock;

    public UpdateDeviceUseCase(DeviceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DeviceId execute(UpdateDeviceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DeviceId id = DeviceId.parse(command.deviceId());
        Device aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("device not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDeviceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String deviceId,
            String reason) {}
}
