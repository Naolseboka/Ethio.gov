package et.gov.ethgov.citizen.domain.addresshistory;

import java.util.List;
import java.util.Optional;

public interface AddresshistoryRepository {
    Addresshistory save(Addresshistory aggregate);

    Optional<Addresshistory> findById(AddresshistoryId id);

    Optional<Addresshistory> findByReferenceCode(String tenantId, String referenceCode);

    List<Addresshistory> findByTenant(String tenantId, int limit);

    void delete(AddresshistoryId id);
}
