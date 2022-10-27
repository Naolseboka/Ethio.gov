package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.util.List;
import java.util.Objects;

public final class ListDispatchUseCase {
    private final DispatchRepository repository;

    public ListDispatchUseCase(DispatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Dispatch> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
