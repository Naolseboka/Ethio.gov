package et.gov.ethgov.opendata.application.opendataset;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import java.util.List;
import java.util.Objects;

public final class ListOpendatasetUseCase {
    private final OpendatasetRepository repository;

    public ListOpendatasetUseCase(OpendatasetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Opendataset> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
