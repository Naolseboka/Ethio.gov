package et.gov.ethgov.developer.domain.devchangelog;

import java.util.List;
import java.util.Optional;

public interface DevchangelogRepository {
    Devchangelog save(Devchangelog aggregate);

    Optional<Devchangelog> findById(DevchangelogId id);

    Optional<Devchangelog> findByReferenceCode(String tenantId, String referenceCode);

    List<Devchangelog> findByTenant(String tenantId, int limit);

    void delete(DevchangelogId id);
}
