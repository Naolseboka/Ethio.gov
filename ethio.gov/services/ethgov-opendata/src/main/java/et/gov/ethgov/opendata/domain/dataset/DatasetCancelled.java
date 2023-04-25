package et.gov.ethgov.opendata.domain.dataset;

import java.time.Instant;

public record DatasetCancelled(DatasetId datasetId, String reason, Instant occurredAt) implements DatasetDomainEvent {}
