package et.gov.ethgov.developer.application.devwebhook;

import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookRepository;
import java.util.List;
import java.util.Objects;

public final class ListDevwebhookUseCase {
    private final DevwebhookRepository repository;

    public ListDevwebhookUseCase(DevwebhookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Devwebhook> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
