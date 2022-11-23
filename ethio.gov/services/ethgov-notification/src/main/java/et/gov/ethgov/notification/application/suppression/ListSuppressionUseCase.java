package et.gov.ethgov.notification.application.suppression;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import java.util.List;
import java.util.Objects;

public final class ListSuppressionUseCase {
    private final SuppressionRepository repository;

    public ListSuppressionUseCase(SuppressionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Suppression> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
