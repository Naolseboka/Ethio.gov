package et.gov.ethgov.opendata.domain.dataset;

import java.time.Instant;

public sealed interface DatasetDomainEvent
        permits DatasetRegistered, DatasetUpdated, DatasetPublished, DatasetRetired, DatasetCancelled {
    DatasetId datasetId();

    Instant occurredAt();
}
