package et.gov.ethgov.analytics.domain.event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event aggregate);

    Optional<Event> findById(EventId id);

    Optional<Event> findByReferenceCode(String tenantId, String referenceCode);

    List<Event> findByTenant(String tenantId, int limit);

    void delete(EventId id);
}
