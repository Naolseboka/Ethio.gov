package et.gov.ethgov.opendata.application.dataupdate;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import java.util.List;
import java.util.Objects;

public final class ListDataupdateUseCase {
    private final DataupdateRepository repository;

    public ListDataupdateUseCase(DataupdateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Dataupdate> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
