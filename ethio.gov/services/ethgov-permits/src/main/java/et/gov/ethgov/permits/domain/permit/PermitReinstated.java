package et.gov.ethgov.permits.domain.permit;
import java.time.Instant;
public record PermitReinstated(PermitId permitId, Instant occurredAt) implements PermitDomainEvent {}
