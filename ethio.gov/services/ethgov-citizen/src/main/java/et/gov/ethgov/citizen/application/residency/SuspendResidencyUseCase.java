package et.gov.ethgov.citizen.application.residency;

import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyId;
import et.gov.ethgov.citizen.domain.residency.ResidencyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendResidencyUseCase {
    private final ResidencyRepository repository;
    private final Clock clock;

    public SuspendResidencyUseCase(ResidencyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ResidencyId execute(SuspendResidencyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ResidencyId id = ResidencyId.parse(command.residencyId());
        Residency aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("residency not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendResidencyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String residencyId,
            String reason) {}
}
