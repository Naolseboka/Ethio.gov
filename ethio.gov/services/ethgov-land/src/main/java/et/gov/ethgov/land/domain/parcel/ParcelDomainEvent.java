package et.gov.ethgov.land.domain.parcel;

import java.time.Instant;

public sealed interface ParcelDomainEvent
        permits ParcelRegistered, ParcelUpdated, ParcelTransferred, ParcelEncumbered,
                ParcelEncumbranceCleared, ParcelDocumentAttached, ParcelRetired, ParcelCancelled {
    ParcelId parcelId();
    Instant occurredAt();
}
