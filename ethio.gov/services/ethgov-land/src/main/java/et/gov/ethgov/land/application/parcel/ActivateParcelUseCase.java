package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateParcelUseCase {
    private final ParcelRepository repository;
    private final Clock clock;

    public ActivateParcelUseCase(ParcelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ParcelId execute(ActivateParcelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ParcelId id = ParcelId.parse(command.parcelId());
        Parcel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("parcel not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateParcelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String parcelId,
            String reason) {}
}
