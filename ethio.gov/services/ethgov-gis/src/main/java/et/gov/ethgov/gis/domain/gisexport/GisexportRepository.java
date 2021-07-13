package et.gov.ethgov.gis.domain.gisexport;

import java.util.List;
import java.util.Optional;

public interface GisexportRepository {
    Gisexport save(Gisexport aggregate);

    Optional<Gisexport> findById(GisexportId id);

    Optional<Gisexport> findByReferenceCode(String tenantId, String referenceCode);

    List<Gisexport> findByTenant(String tenantId, int limit);

    void delete(GisexportId id);
}
