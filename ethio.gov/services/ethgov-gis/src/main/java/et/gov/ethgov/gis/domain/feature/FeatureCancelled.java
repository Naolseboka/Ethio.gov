package et.gov.ethgov.gis.domain.feature;
import java.time.Instant;
public record FeatureCancelled(FeatureId featureId, String reason, Instant occurredAt) implements FeatureDomainEvent {}
