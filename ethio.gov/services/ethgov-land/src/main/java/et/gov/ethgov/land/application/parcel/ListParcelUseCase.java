package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.util.List;
import java.util.Objects;

public final class ListParcelUseCase {
    private final ParcelRepository repository;

    public ListParcelUseCase(ParcelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Parcel> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
