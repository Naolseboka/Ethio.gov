package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateGwrateruleUseCase {
    private final GwrateruleRepository repository;
    private final Clock clock;

    public UpdateGwrateruleUseCase(GwrateruleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GwrateruleId execute(UpdateGwrateruleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GwrateruleId id = GwrateruleId.parse(command.gwrateruleId());
        Gwraterule aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gwraterule not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateGwrateruleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gwrateruleId,
            String reason) {}
}
