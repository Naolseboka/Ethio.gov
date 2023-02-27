package et.gov.ethgov.opendata.application.apicontract;

import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractRepository;
import java.util.List;
import java.util.Objects;

public final class ListApicontractUseCase {
    private final ApicontractRepository repository;

    public ListApicontractUseCase(ApicontractRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Apicontract> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
