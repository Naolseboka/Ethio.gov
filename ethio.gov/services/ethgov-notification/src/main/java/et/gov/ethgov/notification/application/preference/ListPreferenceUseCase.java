package et.gov.ethgov.notification.application.preference;

import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceRepository;
import java.util.List;
import java.util.Objects;

public final class ListPreferenceUseCase {
    private final PreferenceRepository repository;

    public ListPreferenceUseCase(PreferenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Preference> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
