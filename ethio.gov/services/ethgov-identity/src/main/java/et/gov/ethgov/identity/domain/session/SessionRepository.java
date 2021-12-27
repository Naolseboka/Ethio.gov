package et.gov.ethgov.identity.domain.session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session aggregate);

    Optional<Session> findById(SessionId id);

    Optional<Session> findByReferenceCode(String tenantId, String referenceCode);

    List<Session> findByTenant(String tenantId, int limit);

    void delete(SessionId id);
}
