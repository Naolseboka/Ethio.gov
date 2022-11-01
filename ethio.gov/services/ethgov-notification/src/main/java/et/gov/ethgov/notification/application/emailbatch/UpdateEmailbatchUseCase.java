package et.gov.ethgov.notification.application.emailbatch;

import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchId;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateEmailbatchUseCase {
    private final EmailbatchRepository repository;
    private final Clock clock;

    public UpdateEmailbatchUseCase(EmailbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmailbatchId execute(UpdateEmailbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EmailbatchId id = EmailbatchId.parse(command.emailbatchId());
        Emailbatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emailbatch not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateEmailbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emailbatchId,
            String reason) {}
}
