package et.gov.ethgov.notification.application.emailbatch;

import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchId;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEmailbatchUseCase {
    private final EmailbatchRepository repository;
    private final Clock clock;

    public CreateEmailbatchUseCase(EmailbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmailbatchId execute(CreateEmailbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Emailbatch aggregate = Emailbatch.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEmailbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emailbatchId,
            String reason) {}
}
