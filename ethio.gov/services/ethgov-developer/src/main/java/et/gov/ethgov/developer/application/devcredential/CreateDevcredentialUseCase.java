package et.gov.ethgov.developer.application.devcredential;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialId;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDevcredentialUseCase {
    private final DevcredentialRepository repository;
    private final Clock clock;

    public CreateDevcredentialUseCase(DevcredentialRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevcredentialId execute(CreateDevcredentialCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Devcredential aggregate = Devcredential.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDevcredentialCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devcredentialId,
            String reason) {}
}
