package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateEmergencycontactUseCase {
    private final EmergencycontactRepository repository;
    private final Clock clock;

    public UpdateEmergencycontactUseCase(EmergencycontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmergencycontactId execute(UpdateEmergencycontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EmergencycontactId id = EmergencycontactId.parse(command.emergencycontactId());
        Emergencycontact aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emergencycontact not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateEmergencycontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emergencycontactId,
            String reason) {}
}
