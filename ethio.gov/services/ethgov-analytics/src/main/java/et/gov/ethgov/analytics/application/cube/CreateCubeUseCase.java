package et.gov.ethgov.analytics.application.cube;

import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeId;
import et.gov.ethgov.analytics.domain.cube.CubeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCubeUseCase {
    private final CubeRepository repository;
    private final Clock clock;

    public CreateCubeUseCase(CubeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CubeId execute(CreateCubeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Cube aggregate = Cube.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCubeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cubeId,
            String reason) {}
}
