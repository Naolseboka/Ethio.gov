package et.gov.ethgov.permits.application.occupancy;

import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyId;
import et.gov.ethgov.permits.domain.occupancy.OccupancyRepository;
import java.util.Objects;

public final class GetOccupancyUseCase {
    private final OccupancyRepository repository;

    public GetOccupancyUseCase(OccupancyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Occupancy execute(String rawId) {
        OccupancyId id = OccupancyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("occupancy not found: " + rawId));
    }
}
