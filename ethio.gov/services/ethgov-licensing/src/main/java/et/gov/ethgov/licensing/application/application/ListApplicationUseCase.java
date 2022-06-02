package et.gov.ethgov.licensing.application.application;

import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationRepository;
import java.util.List;
import java.util.Objects;

public final class ListApplicationUseCase {
    private final ApplicationRepository repository;

    public ListApplicationUseCase(ApplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Application> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
