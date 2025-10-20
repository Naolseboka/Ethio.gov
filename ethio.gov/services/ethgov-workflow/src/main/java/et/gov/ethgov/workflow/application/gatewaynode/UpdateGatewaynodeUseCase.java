package et.gov.ethgov.workflow.application.gatewaynode;

import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeId;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateGatewaynodeUseCase {
    private final GatewaynodeRepository repository;
    private final Clock clock;

    public UpdateGatewaynodeUseCase(GatewaynodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GatewaynodeId execute(UpdateGatewaynodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GatewaynodeId id = GatewaynodeId.parse(command.gatewaynodeId());
        Gatewaynode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gatewaynode not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateGatewaynodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gatewaynodeId,
            String reason) {}
}
