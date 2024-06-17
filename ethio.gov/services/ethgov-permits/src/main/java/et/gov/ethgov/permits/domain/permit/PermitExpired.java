package et.gov.ethgov.permits.domain.permit;
import java.time.Instant;
public record PermitExpired(PermitId permitId, Instant occurredAt) implements PermitDomainEvent {}
