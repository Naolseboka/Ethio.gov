package et.gov.ethgov.developer.application.sandbox;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import java.util.List;
import java.util.Objects;

public final class ListSandboxUseCase {
    private final SandboxRepository repository;

    public ListSandboxUseCase(SandboxRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Sandbox> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
