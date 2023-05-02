package et.gov.ethgov.opendata.domain.dataset;

import java.time.Instant;

public record DatasetUpdated(DatasetId datasetId, Instant occurredAt) implements DatasetDomainEvent {}
