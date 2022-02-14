package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import java.util.Objects;

public final class GetParcelUseCase {
    private final ParcelRepository repository;

    public GetParcelUseCase(ParcelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Parcel execute(String rawId) {
        ParcelId id = ParcelId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("parcel not found: " + rawId));
    }
}
