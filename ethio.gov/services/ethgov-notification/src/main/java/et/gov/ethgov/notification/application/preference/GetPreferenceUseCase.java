package et.gov.ethgov.notification.application.preference;

import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceId;
import et.gov.ethgov.notification.domain.preference.PreferenceRepository;
import java.util.Objects;

public final class GetPreferenceUseCase {
    private final PreferenceRepository repository;

    public GetPreferenceUseCase(PreferenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Preference execute(String rawId) {
        PreferenceId id = PreferenceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("preference not found: " + rawId));
    }
}
