package et.gov.ethgov.directory.application.facility;

import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityId;
import et.gov.ethgov.directory.domain.facility.FacilityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateFacilityUseCase {
    private final FacilityRepository repository;
    private final Clock clock;

    public CreateFacilityUseCase(FacilityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FacilityId execute(CreateFacilityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Facility aggregate = Facility.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateFacilityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String facilityId,
            String reason) {}
}
