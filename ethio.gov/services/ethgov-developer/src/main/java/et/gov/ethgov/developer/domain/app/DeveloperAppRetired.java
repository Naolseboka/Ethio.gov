package et.gov.ethgov.developer.domain.app;

import java.time.Instant;

public record DeveloperAppRetired(DeveloperAppId developerAppId, Instant occurredAt) implements DeveloperAppDomainEvent {}
