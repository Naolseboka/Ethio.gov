package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateEmergencycontactUseCase {
    private final EmergencycontactRepository repository;
    private final Clock clock;

    public ActivateEmergencycontactUseCase(EmergencycontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmergencycontactId execute(ActivateEmergencycontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EmergencycontactId id = EmergencycontactId.parse(command.emergencycontactId());
        Emergencycontact aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emergencycontact not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateEmergencycontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emergencycontactId,
            String reason) {}
}
