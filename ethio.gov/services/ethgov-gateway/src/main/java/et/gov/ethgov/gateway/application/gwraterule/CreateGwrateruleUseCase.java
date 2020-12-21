package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateGwrateruleUseCase {
    private final GwrateruleRepository repository;
    private final Clock clock;

    public CreateGwrateruleUseCase(GwrateruleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GwrateruleId execute(CreateGwrateruleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Gwraterule aggregate = Gwraterule.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateGwrateruleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gwrateruleId,
            String reason) {}
}
