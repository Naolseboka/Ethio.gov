package et.gov.ethgov.permits.application.demolition;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import java.util.List;
import java.util.Objects;

public final class ListDemolitionUseCase {
    private final DemolitionRepository repository;

    public ListDemolitionUseCase(DemolitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Demolition> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
