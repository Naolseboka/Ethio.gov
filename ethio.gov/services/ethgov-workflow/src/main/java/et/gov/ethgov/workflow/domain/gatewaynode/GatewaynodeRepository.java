package et.gov.ethgov.workflow.domain.gatewaynode;

import java.util.List;
import java.util.Optional;

public interface GatewaynodeRepository {
    Gatewaynode save(Gatewaynode aggregate);

    Optional<Gatewaynode> findById(GatewaynodeId id);

    Optional<Gatewaynode> findByReferenceCode(String tenantId, String referenceCode);

    List<Gatewaynode> findByTenant(String tenantId, int limit);

    void delete(GatewaynodeId id);
}
