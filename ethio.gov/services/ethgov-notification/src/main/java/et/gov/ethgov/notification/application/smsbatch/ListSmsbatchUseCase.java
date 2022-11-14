package et.gov.ethgov.notification.application.smsbatch;

import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchRepository;
import java.util.List;
import java.util.Objects;

public final class ListSmsbatchUseCase {
    private final SmsbatchRepository repository;

    public ListSmsbatchUseCase(SmsbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Smsbatch> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
