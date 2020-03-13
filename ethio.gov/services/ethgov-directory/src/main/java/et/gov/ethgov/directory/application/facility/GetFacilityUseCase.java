package et.gov.ethgov.directory.application.facility;

import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityId;
import et.gov.ethgov.directory.domain.facility.FacilityRepository;
import java.util.Objects;

public final class GetFacilityUseCase {
    private final FacilityRepository repository;

    public GetFacilityUseCase(FacilityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Facility execute(String rawId) {
        FacilityId id = FacilityId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("facility not found: " + rawId));
    }
}
