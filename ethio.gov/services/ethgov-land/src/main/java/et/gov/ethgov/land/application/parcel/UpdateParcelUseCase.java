package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateParcelUseCase {
    private final ParcelRepository repository;
    private final Clock clock;

    public UpdateParcelUseCase(ParcelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ParcelId execute(UpdateParcelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ParcelId id = ParcelId.parse(command.parcelId());
        Parcel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("parcel not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateParcelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String parcelId,
            String reason) {}
}
