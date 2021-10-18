package et.gov.ethgov.identity.application.session;

import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionRepository;
import java.util.List;
import java.util.Objects;

public final class ListSessionUseCase {
    private final SessionRepository repository;

    public ListSessionUseCase(SessionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Session> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
