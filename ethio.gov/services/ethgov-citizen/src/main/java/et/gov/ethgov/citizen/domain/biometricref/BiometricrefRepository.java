package et.gov.ethgov.citizen.domain.biometricref;

import java.util.List;
import java.util.Optional;

public interface BiometricrefRepository {
    Biometricref save(Biometricref aggregate);

    Optional<Biometricref> findById(BiometricrefId id);

    Optional<Biometricref> findByReferenceCode(String tenantId, String referenceCode);

    List<Biometricref> findByTenant(String tenantId, int limit);

    void delete(BiometricrefId id);
}
