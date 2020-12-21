package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendGwrateruleUseCase {
    private final GwrateruleRepository repository;
    private final Clock clock;

    public SuspendGwrateruleUseCase(GwrateruleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GwrateruleId execute(SuspendGwrateruleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GwrateruleId id = GwrateruleId.parse(command.gwrateruleId());
        Gwraterule aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gwraterule not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendGwrateruleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gwrateruleId,
            String reason) {}
}
