package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEmergencycontactUseCase {
    private final EmergencycontactRepository repository;
    private final Clock clock;

    public CreateEmergencycontactUseCase(EmergencycontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EmergencycontactId execute(CreateEmergencycontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Emergencycontact aggregate = Emergencycontact.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEmergencycontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String emergencycontactId,
            String reason) {}
}
