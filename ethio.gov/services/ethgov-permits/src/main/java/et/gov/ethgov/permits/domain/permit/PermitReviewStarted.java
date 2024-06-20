package et.gov.ethgov.permits.domain.permit;
import java.time.Instant;
public record PermitReviewStarted(PermitId permitId, Instant occurredAt) implements PermitDomainEvent {}
