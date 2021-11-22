package et.gov.ethgov.identity.domain.device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Device save(Device aggregate);

    Optional<Device> findById(DeviceId id);

    Optional<Device> findByReferenceCode(String tenantId, String referenceCode);

    List<Device> findByTenant(String tenantId, int limit);

    void delete(DeviceId id);
}
