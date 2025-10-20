package et.gov.ethgov.workflow.application.gatewaynode;

import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeRepository;
import java.util.List;
import java.util.Objects;

public final class ListGatewaynodeUseCase {
    private final GatewaynodeRepository repository;

    public ListGatewaynodeUseCase(GatewaynodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Gatewaynode> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
