package et.gov.ethgov.directory.domain.regionnode;

import java.util.List;
import java.util.Optional;

public interface RegionnodeRepository {
    Regionnode save(Regionnode aggregate);

    Optional<Regionnode> findById(RegionnodeId id);

    Optional<Regionnode> findByReferenceCode(String tenantId, String referenceCode);

    List<Regionnode> findByTenant(String tenantId, int limit);

    void delete(RegionnodeId id);
}
