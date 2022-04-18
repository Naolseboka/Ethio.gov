package et.gov.ethgov.land.domain.parcel;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository {
    Parcel save(Parcel aggregate);

    Optional<Parcel> findById(ParcelId id);

    Optional<Parcel> findByReferenceCode(String tenantId, String referenceCode);

    List<Parcel> findByTenant(String tenantId, int limit);

    void delete(ParcelId id);
}
