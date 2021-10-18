package et.gov.ethgov.identity.application.session;

import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionId;
import et.gov.ethgov.identity.domain.session.SessionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSessionUseCase {
    private final SessionRepository repository;
    private final Clock clock;

    public CreateSessionUseCase(SessionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SessionId execute(CreateSessionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Session aggregate = Session.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSessionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sessionId,
            String reason) {}
}
