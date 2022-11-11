package et.gov.ethgov.notification.application.preference;

import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceId;
import et.gov.ethgov.notification.domain.preference.PreferenceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePreferenceUseCase {
    private final PreferenceRepository repository;
    private final Clock clock;

    public UpdatePreferenceUseCase(PreferenceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PreferenceId execute(UpdatePreferenceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PreferenceId id = PreferenceId.parse(command.preferenceId());
        Preference aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("preference not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePreferenceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String preferenceId,
            String reason) {}
}
