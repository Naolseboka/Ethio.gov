package et.gov.ethgov.opendata.application.harvest;

import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestRepository;
import java.util.List;
import java.util.Objects;

public final class ListHarvestUseCase {
    private final HarvestRepository repository;

    public ListHarvestUseCase(HarvestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Harvest> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
