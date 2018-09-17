package et.gov.ethgov.analytics.application.event;

import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventRepository;
import java.util.List;
import java.util.Objects;

public final class ListEventUseCase {
    private final EventRepository repository;

    public ListEventUseCase(EventRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Event> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
