package et.gov.ethgov.opendata.domain.resource;

import java.time.Instant;

public record ResourceRetired(ResourceId resourceId, String reason, Instant occurredAt)
        implements ResourceDomainEvent {}
