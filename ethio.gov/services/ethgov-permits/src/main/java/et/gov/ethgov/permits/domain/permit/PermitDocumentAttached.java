package et.gov.ethgov.permits.domain.permit;
import java.time.Instant;
public record PermitDocumentAttached(PermitId permitId, String documentId, Instant occurredAt)
        implements PermitDomainEvent {}
