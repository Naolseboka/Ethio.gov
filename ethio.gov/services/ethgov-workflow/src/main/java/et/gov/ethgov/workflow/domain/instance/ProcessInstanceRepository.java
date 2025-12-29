package et.gov.ethgov.workflow.domain.instance;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface ProcessInstanceRepository {

    boolean existsByTenantAndBusinessKey(TenantId tenantId, String businessKey);

    Optional<ProcessInstance> findById(ProcessInstanceId id);

    void save(ProcessInstance instance);
}
