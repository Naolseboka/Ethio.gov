package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateParcelUseCase {
    private final ParcelRepository repository;
    private final Clock clock;

    public CreateParcelUseCase(ParcelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ParcelId execute(CreateParcelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Parcel aggregate = Parcel.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateParcelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String parcelId,
            String reason) {}
}
