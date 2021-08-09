package et.gov.ethgov.gis.domain.tilecache;

import java.util.List;
import java.util.Optional;

public interface TilecacheRepository {
    Tilecache save(Tilecache aggregate);

    Optional<Tilecache> findById(TilecacheId id);

    Optional<Tilecache> findByReferenceCode(String tenantId, String referenceCode);

    List<Tilecache> findByTenant(String tenantId, int limit);

    void delete(TilecacheId id);
}
