package et.gov.ethgov.opendata.domain.resource;

import java.time.Instant;

public sealed interface ResourceDomainEvent
        permits ResourceAdded, ResourceUpdated, ResourceRetired {
    ResourceId resourceId();

    Instant occurredAt();
}
