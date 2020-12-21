package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateGwrateruleUseCase {
    private final GwrateruleRepository repository;
    private final Clock clock;

    public ActivateGwrateruleUseCase(GwrateruleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GwrateruleId execute(ActivateGwrateruleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GwrateruleId id = GwrateruleId.parse(command.gwrateruleId());
        Gwraterule aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gwraterule not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateGwrateruleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gwrateruleId,
            String reason) {}
}
