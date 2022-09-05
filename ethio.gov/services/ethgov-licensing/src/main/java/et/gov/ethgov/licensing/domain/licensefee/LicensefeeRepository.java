package et.gov.ethgov.licensing.domain.licensefee;

import java.util.List;
import java.util.Optional;

public interface LicensefeeRepository {
    Licensefee save(Licensefee aggregate);

    Optional<Licensefee> findById(LicensefeeId id);

    Optional<Licensefee> findByReferenceCode(String tenantId, String referenceCode);

    List<Licensefee> findByTenant(String tenantId, int limit);

    void delete(LicensefeeId id);
}
