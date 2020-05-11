package et.gov.ethgov.directory.domain.kiosk;

import java.util.List;
import java.util.Optional;

public interface KioskRepository {
    Kiosk save(Kiosk aggregate);

    Optional<Kiosk> findById(KioskId id);

    Optional<Kiosk> findByReferenceCode(String tenantId, String referenceCode);

    List<Kiosk> findByTenant(String tenantId, int limit);

    void delete(KioskId id);
}
