package et.gov.ethgov.licensing.domain.application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application aggregate);

    Optional<Application> findById(ApplicationId id);

    Optional<Application> findByReferenceCode(String tenantId, String referenceCode);

    List<Application> findByTenant(String tenantId, int limit);

    void delete(ApplicationId id);
}
