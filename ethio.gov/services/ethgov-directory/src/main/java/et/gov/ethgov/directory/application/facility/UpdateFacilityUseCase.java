package et.gov.ethgov.directory.application.facility;

import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityId;
import et.gov.ethgov.directory.domain.facility.FacilityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateFacilityUseCase {
    private final FacilityRepository repository;
    private final Clock clock;

    public UpdateFacilityUseCase(FacilityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FacilityId execute(UpdateFacilityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FacilityId id = FacilityId.parse(command.facilityId());
        Facility aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("facility not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateFacilityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String facilityId,
            String reason) {}
}
