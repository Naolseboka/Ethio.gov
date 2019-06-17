package et.gov.ethgov.citizen.application.citizenship;

import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipId;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCitizenshipUseCase {
    private final CitizenshipRepository repository;
    private final Clock clock;

    public UpdateCitizenshipUseCase(CitizenshipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CitizenshipId execute(UpdateCitizenshipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CitizenshipId id = CitizenshipId.parse(command.citizenshipId());
        Citizenship aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("citizenship not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCitizenshipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String citizenshipId,
            String reason) {}
}
