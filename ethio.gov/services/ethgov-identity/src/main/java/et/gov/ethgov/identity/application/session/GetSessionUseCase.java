package et.gov.ethgov.identity.application.session;

import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionId;
import et.gov.ethgov.identity.domain.session.SessionRepository;
import java.util.Objects;

public final class GetSessionUseCase {
    private final SessionRepository repository;

    public GetSessionUseCase(SessionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Session execute(String rawId) {
        SessionId id = SessionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + rawId));
    }
}
