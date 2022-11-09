package et.gov.ethgov.notification.application.preference;

import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceId;
import et.gov.ethgov.notification.domain.preference.PreferenceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePreferenceUseCase {
    private final PreferenceRepository repository;
    private final Clock clock;

    public CreatePreferenceUseCase(PreferenceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PreferenceId execute(CreatePreferenceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Preference aggregate = Preference.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePreferenceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String preferenceId,
            String reason) {}
}
