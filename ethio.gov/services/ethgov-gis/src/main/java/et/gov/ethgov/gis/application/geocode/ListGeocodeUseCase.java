package et.gov.ethgov.gis.application.geocode;

import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeRepository;
import java.util.List;
import java.util.Objects;

public final class ListGeocodeUseCase {
    private final GeocodeRepository repository;

    public ListGeocodeUseCase(GeocodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Geocode> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
