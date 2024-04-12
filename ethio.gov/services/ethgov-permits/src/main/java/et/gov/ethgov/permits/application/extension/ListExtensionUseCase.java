package et.gov.ethgov.permits.application.extension;

import et.gov.ethgov.permits.domain.extension.Extension;
import et.gov.ethgov.permits.domain.extension.ExtensionRepository;
import java.util.List;
import java.util.Objects;

public final class ListExtensionUseCase {
    private final ExtensionRepository repository;

    public ListExtensionUseCase(ExtensionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Extension> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
