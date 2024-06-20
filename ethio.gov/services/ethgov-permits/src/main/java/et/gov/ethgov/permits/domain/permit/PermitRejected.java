package et.gov.ethgov.permits.domain.permit;
import java.time.Instant;
public record PermitRejected(PermitId permitId, String reason, Instant occurredAt) implements PermitDomainEvent {}
