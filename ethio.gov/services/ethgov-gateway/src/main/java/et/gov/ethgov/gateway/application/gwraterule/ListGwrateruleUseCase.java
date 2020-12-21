package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.util.List;
import java.util.Objects;

public final class ListGwrateruleUseCase {
    private final GwrateruleRepository repository;

    public ListGwrateruleUseCase(GwrateruleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Gwraterule> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
