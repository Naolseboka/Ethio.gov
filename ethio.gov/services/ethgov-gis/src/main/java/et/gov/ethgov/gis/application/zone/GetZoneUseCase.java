package et.gov.ethgov.gis.application.zone;

import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneId;
import et.gov.ethgov.gis.domain.zone.ZoneRepository;
import java.util.Objects;

public final class GetZoneUseCase {
    private final ZoneRepository repository;

    public GetZoneUseCase(ZoneRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Zone execute(String rawId) {
        ZoneId id = ZoneId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("zone not found: " + rawId));
    }
}
