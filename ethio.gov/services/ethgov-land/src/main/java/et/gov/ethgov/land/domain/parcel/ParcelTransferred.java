package et.gov.ethgov.land.domain.parcel;
import java.time.Instant;
public record ParcelTransferred(ParcelId parcelId, Instant occurredAt) implements ParcelDomainEvent {}
