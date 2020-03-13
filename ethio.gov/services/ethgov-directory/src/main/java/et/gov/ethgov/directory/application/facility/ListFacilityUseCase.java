package et.gov.ethgov.directory.application.facility;

import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityRepository;
import java.util.List;
import java.util.Objects;

public final class ListFacilityUseCase {
    private final FacilityRepository repository;

    public ListFacilityUseCase(FacilityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Facility> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
