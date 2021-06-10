package et.gov.ethgov.gis.application.zone;

import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneRepository;
import java.util.List;
import java.util.Objects;

public final class ListZoneUseCase {
    private final ZoneRepository repository;

    public ListZoneUseCase(ZoneRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Zone> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
