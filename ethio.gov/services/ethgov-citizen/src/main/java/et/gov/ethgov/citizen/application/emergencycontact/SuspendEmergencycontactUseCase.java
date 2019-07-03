package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendEmergencycontactUseCase {
    private final EmergencycontactRepository repository;
    private final Clock clock;

    public SuspendEmergencycontactUseCase(EmergencycontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmergencycontactId execute(SuspendEmergencycontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EmergencycontactId id = EmergencycontactId.parse(command.emergencycontactId());
        Emergencycontact aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emergencycontact not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendEmergencycontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emergencycontactId,
            String reason) {}
}
