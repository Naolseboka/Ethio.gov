package et.gov.ethgov.gis.domain.geocode;

import java.util.List;
import java.util.Optional;

public interface GeocodeRepository {
    Geocode save(Geocode aggregate);

    Optional<Geocode> findById(GeocodeId id);

    Optional<Geocode> findByReferenceCode(String tenantId, String referenceCode);

    List<Geocode> findByTenant(String tenantId, int limit);

    void delete(GeocodeId id);
}
