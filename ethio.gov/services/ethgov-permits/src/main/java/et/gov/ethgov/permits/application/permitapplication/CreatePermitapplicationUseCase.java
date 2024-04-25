package et.gov.ethgov.permits.application.permitapplication;

import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationId;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePermitapplicationUseCase {
    private final PermitapplicationRepository repository;
    private final Clock clock;

    public CreatePermitapplicationUseCase(PermitapplicationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitapplicationId execute(CreatePermitapplicationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Permitapplication aggregate = Permitapplication.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePermitapplicationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitapplicationId,
            String reason) {}
}
