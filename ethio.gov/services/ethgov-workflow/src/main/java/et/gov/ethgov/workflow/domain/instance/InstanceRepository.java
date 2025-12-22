package et.gov.ethgov.workflow.domain.instance;

import java.util.List;
import java.util.Optional;

public interface InstanceRepository {
    Instance save(Instance aggregate);

    Optional<Instance> findById(InstanceId id);

    Optional<Instance> findByReferenceCode(String tenantId, String referenceCode);

    List<Instance> findByTenant(String tenantId, int limit);

    void delete(InstanceId id);
}
