package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendParcelUseCase {
    private final ParcelRepository repository;
    private final Clock clock;

    public SuspendParcelUseCase(ParcelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ParcelId execute(SuspendParcelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ParcelId id = ParcelId.parse(command.parcelId());
        Parcel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("parcel not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendParcelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String parcelId,
            String reason) {}
}
