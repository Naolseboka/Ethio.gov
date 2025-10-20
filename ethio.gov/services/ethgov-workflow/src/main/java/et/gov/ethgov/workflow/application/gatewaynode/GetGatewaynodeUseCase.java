package et.gov.ethgov.workflow.application.gatewaynode;

import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeId;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeRepository;
import java.util.Objects;

public final class GetGatewaynodeUseCase {
    private final GatewaynodeRepository repository;

    public GetGatewaynodeUseCase(GatewaynodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Gatewaynode execute(String rawId) {
        GatewaynodeId id = GatewaynodeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gatewaynode not found: " + rawId));
    }
}
