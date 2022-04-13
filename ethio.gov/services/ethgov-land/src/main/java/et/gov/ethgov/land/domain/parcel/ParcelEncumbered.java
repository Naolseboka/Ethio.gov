package et.gov.ethgov.land.domain.parcel;
import java.time.Instant;
public record ParcelEncumbered(ParcelId parcelId, String reason, Instant occurredAt) implements ParcelDomainEvent {}
