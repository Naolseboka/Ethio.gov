package et.gov.ethgov.land.domain.parcel;
import java.time.Instant;
public record ParcelDocumentAttached(ParcelId parcelId, String documentId, Instant occurredAt) implements ParcelDomainEvent {}
