package et.gov.ethgov.identity.application.session;

import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionId;
import et.gov.ethgov.identity.domain.session.SessionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSessionUseCase {
    private final SessionRepository repository;
    private final Clock clock;

    public ActivateSessionUseCase(SessionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SessionId execute(ActivateSessionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SessionId id = SessionId.parse(command.sessionId());
        Session aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSessionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sessionId,
            String reason) {}
}
