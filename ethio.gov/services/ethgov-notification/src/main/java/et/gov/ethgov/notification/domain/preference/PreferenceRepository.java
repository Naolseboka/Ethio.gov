package et.gov.ethgov.notification.domain.preference;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepository {
    Preference save(Preference aggregate);

    Optional<Preference> findById(PreferenceId id);

    Optional<Preference> findByReferenceCode(String tenantId, String referenceCode);

    List<Preference> findByTenant(String tenantId, int limit);

    void delete(PreferenceId id);
}
