package et.gov.ethgov.opendata.application.portalpage;

import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageRepository;
import java.util.List;
import java.util.Objects;

public final class ListPortalpageUseCase {
    private final PortalpageRepository repository;

    public ListPortalpageUseCase(PortalpageRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Portalpage> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
