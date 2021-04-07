package et.gov.ethgov.gateway.domain.upstream;

import java.util.List;
import java.util.Optional;

public interface UpstreamRepository {
    Upstream save(Upstream aggregate);

    Optional<Upstream> findById(UpstreamId id);

    Optional<Upstream> findByReferenceCode(String tenantId, String referenceCode);

    List<Upstream> findByTenant(String tenantId, int limit);

    void delete(UpstreamId id);
}
