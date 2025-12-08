package et.gov.ethgov.workflow.domain.definition;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface WorkflowDefinitionRepository {

    boolean existsByTenantAndKey(TenantId tenantId, DefinitionKey key);

    Optional<WorkflowDefinition> findById(WorkflowDefinitionId id);

    Optional<WorkflowDefinition> findPublishedByTenantAndKey(TenantId tenantId, DefinitionKey key);

    void save(WorkflowDefinition definition);
}
