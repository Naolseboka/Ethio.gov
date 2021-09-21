package et.gov.ethgov.identity.application.device;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceId;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDeviceUseCase {
    private final DeviceRepository repository;
    private final Clock clock;

    public ActivateDeviceUseCase(DeviceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DeviceId execute(ActivateDeviceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DeviceId id = DeviceId.parse(command.deviceId());
        Device aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("device not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDeviceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String deviceId,
            String reason) {}
}
