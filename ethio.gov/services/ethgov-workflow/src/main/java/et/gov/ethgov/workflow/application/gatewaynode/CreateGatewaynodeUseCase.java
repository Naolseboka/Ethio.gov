package et.gov.ethgov.workflow.application.gatewaynode;

import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeId;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateGatewaynodeUseCase {
    private final GatewaynodeRepository repository;
    private final Clock clock;

    public CreateGatewaynodeUseCase(GatewaynodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GatewaynodeId execute(CreateGatewaynodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Gatewaynode aggregate = Gatewaynode.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateGatewaynodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gatewaynodeId,
            String reason) {}
}
