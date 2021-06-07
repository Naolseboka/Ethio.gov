package et.gov.ethgov.gis.application.tilecache;

import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheRepository;
import java.util.List;
import java.util.Objects;

public final class ListTilecacheUseCase {
    private final TilecacheRepository repository;

    public ListTilecacheUseCase(TilecacheRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Tilecache> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
