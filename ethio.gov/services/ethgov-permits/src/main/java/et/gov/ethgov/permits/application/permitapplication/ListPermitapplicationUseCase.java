package et.gov.ethgov.permits.application.permitapplication;

import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationRepository;
import java.util.List;
import java.util.Objects;

public final class ListPermitapplicationUseCase {
    private final PermitapplicationRepository repository;

    public ListPermitapplicationUseCase(PermitapplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Permitapplication> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
