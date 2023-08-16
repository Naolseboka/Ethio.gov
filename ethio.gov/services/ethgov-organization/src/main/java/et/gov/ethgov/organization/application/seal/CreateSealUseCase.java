package et.gov.ethgov.organization.application.seal;

import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealId;
import et.gov.ethgov.organization.domain.seal.SealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSealUseCase {
    private final SealRepository repository;
    private final Clock clock;

    public CreateSealUseCase(SealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SealId execute(CreateSealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Seal aggregate = Seal.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sealId,
            String reason) {}
}
