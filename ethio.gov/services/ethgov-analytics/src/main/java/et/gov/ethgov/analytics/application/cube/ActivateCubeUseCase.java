package et.gov.ethgov.analytics.application.cube;

import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeId;
import et.gov.ethgov.analytics.domain.cube.CubeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCubeUseCase {
    private final CubeRepository repository;
    private final Clock clock;

    public ActivateCubeUseCase(CubeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CubeId execute(ActivateCubeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CubeId id = CubeId.parse(command.cubeId());
        Cube aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cube not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCubeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cubeId,
            String reason) {}
}
