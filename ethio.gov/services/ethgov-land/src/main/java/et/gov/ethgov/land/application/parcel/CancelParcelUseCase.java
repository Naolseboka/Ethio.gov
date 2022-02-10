package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.application.LandEventPublisher;
import et.gov.ethgov.land.domain.parcel.LandParcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelParcelUseCase {

    private final ParcelRepository parcels;
    private final LandEventPublisher eventPublisher;
    private final Clock clock;

    public CancelParcelUseCase(ParcelRepository parcels, LandEventPublisher eventPublisher, Clock clock) {
        this.parcels = parcels;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String parcelId, String reason) {
        Objects.requireNonNull(parcelId, "parcelId");
        LandParcel parcel = parcels.findById(ParcelId.of(parcelId))
                .orElseThrow(() -> new DomainException(
                        "PARCEL_NOT_FOUND", "Parcel Not Found", "Parcel does not exist", 404));
        parcel.cancel(reason, clock.instant());
        parcels.save(parcel);
        eventPublisher.publishAll(parcel.pullDomainEvents());
    }
}
