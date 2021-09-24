package et.gov.ethgov.identity.application.device;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import java.util.List;
import java.util.Objects;

public final class ListDeviceUseCase {
    private final DeviceRepository repository;

    public ListDeviceUseCase(DeviceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Device> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
