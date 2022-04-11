package et.gov.ethgov.land.domain.parcel;
import java.time.Instant;
public record ParcelCancelled(ParcelId parcelId, String reason, Instant occurredAt) implements ParcelDomainEvent {}
