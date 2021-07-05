package et.gov.ethgov.gis.domain.feature;
import java.time.Instant;
public record FeaturePublished(FeatureId featureId, Instant occurredAt) implements FeatureDomainEvent {}
