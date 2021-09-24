package et.gov.ethgov.identity.application.device;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceId;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import java.util.Objects;

public final class GetDeviceUseCase {
    private final DeviceRepository repository;

    public GetDeviceUseCase(DeviceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Device execute(String rawId) {
        DeviceId id = DeviceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("device not found: " + rawId));
    }
}
