package et.gov.ethgov.gis.domain.overlay;

import java.util.List;
import java.util.Optional;

public interface OverlayRepository {
    Overlay save(Overlay aggregate);

    Optional<Overlay> findById(OverlayId id);

    Optional<Overlay> findByReferenceCode(String tenantId, String referenceCode);

    List<Overlay> findByTenant(String tenantId, int limit);

    void delete(OverlayId id);
}
