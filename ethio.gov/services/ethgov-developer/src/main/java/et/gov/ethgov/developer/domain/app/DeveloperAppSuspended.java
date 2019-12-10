package et.gov.ethgov.developer.domain.app;

import java.time.Instant;

public record DeveloperAppSuspended(DeveloperAppId developerAppId, String reason, Instant occurredAt) implements DeveloperAppDomainEvent {}
