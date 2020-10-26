package et.gov.ethgov.document.domain.scanbatch;

import java.util.List;
import java.util.Optional;

public interface ScanbatchRepository {
    Scanbatch save(Scanbatch aggregate);

    Optional<Scanbatch> findById(ScanbatchId id);

    Optional<Scanbatch> findByReferenceCode(String tenantId, String referenceCode);

    List<Scanbatch> findByTenant(String tenantId, int limit);

    void delete(ScanbatchId id);
}
