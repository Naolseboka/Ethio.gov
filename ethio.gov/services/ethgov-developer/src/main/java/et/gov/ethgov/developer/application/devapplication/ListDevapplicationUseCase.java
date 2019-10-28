package et.gov.ethgov.developer.application.devapplication;

import et.gov.ethgov.developer.domain.devapplication.Devapplication;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationRepository;
import java.util.List;
import java.util.Objects;

public final class ListDevapplicationUseCase {
    private final DevapplicationRepository repository;

    public ListDevapplicationUseCase(DevapplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Devapplication> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
