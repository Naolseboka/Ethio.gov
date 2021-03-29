package et.gov.ethgov.gateway.domain.rewrite;

import java.util.List;
import java.util.Optional;

public interface RewriteRepository {
    Rewrite save(Rewrite aggregate);

    Optional<Rewrite> findById(RewriteId id);

    Optional<Rewrite> findByReferenceCode(String tenantId, String referenceCode);

    List<Rewrite> findByTenant(String tenantId, int limit);

    void delete(RewriteId id);
}
