package et.gov.ethgov.permits.application.permitapplication;

import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationId;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePermitapplicationUseCase {
    private final PermitapplicationRepository repository;
    private final Clock clock;

    public ActivatePermitapplicationUseCase(PermitapplicationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitapplicationId execute(ActivatePermitapplicationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitapplicationId id = PermitapplicationId.parse(command.permitapplicationId());
        Permitapplication aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitapplication not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePermitapplicationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitapplicationId,
            String reason) {}
}
