package et.gov.ethgov.citizen.domain.emergencycontact;

import java.util.List;
import java.util.Optional;

public interface EmergencycontactRepository {
    Emergencycontact save(Emergencycontact aggregate);

    Optional<Emergencycontact> findById(EmergencycontactId id);

    Optional<Emergencycontact> findByReferenceCode(String tenantId, String referenceCode);

    List<Emergencycontact> findByTenant(String tenantId, int limit);

    void delete(EmergencycontactId id);
}
