package et.gov.ethgov.gis.domain.feature;
import java.time.Instant;
public record FeatureRetired(FeatureId featureId, Instant occurredAt) implements FeatureDomainEvent {}
