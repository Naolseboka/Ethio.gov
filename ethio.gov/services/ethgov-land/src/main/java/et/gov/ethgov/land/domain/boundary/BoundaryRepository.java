package et.gov.ethgov.land.domain.boundary;

import java.util.List;
import java.util.Optional;

public interface BoundaryRepository {
    Boundary save(Boundary aggregate);

    Optional<Boundary> findById(BoundaryId id);

    Optional<Boundary> findByReferenceCode(String tenantId, String referenceCode);

    List<Boundary> findByTenant(String tenantId, int limit);

    void delete(BoundaryId id);
}
