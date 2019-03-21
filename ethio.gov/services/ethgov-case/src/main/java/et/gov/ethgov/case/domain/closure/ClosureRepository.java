package et.gov.ethgov.case.domain.closure;

import java.util.List;
import java.util.Optional;

public interface ClosureRepository {
    Closure save(Closure aggregate);

    Optional<Closure> findById(ClosureId id);

    Optional<Closure> findByReferenceCode(String tenantId, String referenceCode);

    List<Closure> findByTenant(String tenantId, int limit);

    void delete(ClosureId id);
}
