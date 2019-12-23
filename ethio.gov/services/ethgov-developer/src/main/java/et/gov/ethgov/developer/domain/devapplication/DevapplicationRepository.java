package et.gov.ethgov.developer.domain.devapplication;

import java.util.List;
import java.util.Optional;

public interface DevapplicationRepository {
    Devapplication save(Devapplication aggregate);

    Optional<Devapplication> findById(DevapplicationId id);

    Optional<Devapplication> findByReferenceCode(String tenantId, String referenceCode);

    List<Devapplication> findByTenant(String tenantId, int limit);

    void delete(DevapplicationId id);
}
