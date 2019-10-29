package et.gov.ethgov.developer.application.devchangelog;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import java.util.List;
import java.util.Objects;

public final class ListDevchangelogUseCase {
    private final DevchangelogRepository repository;

    public ListDevchangelogUseCase(DevchangelogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Devchangelog> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
