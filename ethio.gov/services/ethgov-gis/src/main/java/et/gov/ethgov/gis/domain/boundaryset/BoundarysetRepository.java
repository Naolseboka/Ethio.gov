package et.gov.ethgov.gis.domain.boundaryset;

import java.util.List;
import java.util.Optional;

public interface BoundarysetRepository {
    Boundaryset save(Boundaryset aggregate);

    Optional<Boundaryset> findById(BoundarysetId id);

    Optional<Boundaryset> findByReferenceCode(String tenantId, String referenceCode);

    List<Boundaryset> findByTenant(String tenantId, int limit);

    void delete(BoundarysetId id);
}
