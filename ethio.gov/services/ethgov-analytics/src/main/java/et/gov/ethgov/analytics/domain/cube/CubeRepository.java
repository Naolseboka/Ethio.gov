package et.gov.ethgov.analytics.domain.cube;

import java.util.List;
import java.util.Optional;

public interface CubeRepository {
    Cube save(Cube aggregate);

    Optional<Cube> findById(CubeId id);

    Optional<Cube> findByReferenceCode(String tenantId, String referenceCode);

    List<Cube> findByTenant(String tenantId, int limit);

    void delete(CubeId id);
}
