package et.gov.ethgov.identity.application.session;

import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionId;
import et.gov.ethgov.identity.domain.session.SessionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSessionUseCase {
    private final SessionRepository repository;
    private final Clock clock;

    public UpdateSessionUseCase(SessionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SessionId execute(UpdateSessionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SessionId id = SessionId.parse(command.sessionId());
        Session aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSessionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sessionId,
            String reason) {}
}
