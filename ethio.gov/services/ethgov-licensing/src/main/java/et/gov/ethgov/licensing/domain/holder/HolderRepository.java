package et.gov.ethgov.licensing.domain.holder;

import java.util.List;
import java.util.Optional;

public interface HolderRepository {
    Holder save(Holder aggregate);

    Optional<Holder> findById(HolderId id);

    Optional<Holder> findByReferenceCode(String tenantId, String referenceCode);

    List<Holder> findByTenant(String tenantId, int limit);

    void delete(HolderId id);
}
