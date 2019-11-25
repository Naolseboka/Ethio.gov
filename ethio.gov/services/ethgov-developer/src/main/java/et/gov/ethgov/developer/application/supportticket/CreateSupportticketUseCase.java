package et.gov.ethgov.developer.application.supportticket;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketId;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSupportticketUseCase {
    private final SupportticketRepository repository;
    private final Clock clock;

    public CreateSupportticketUseCase(SupportticketRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SupportticketId execute(CreateSupportticketCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Supportticket aggregate = Supportticket.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSupportticketCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String supportticketId,
            String reason) {}
}
