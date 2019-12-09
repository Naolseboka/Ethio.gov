package et.gov.ethgov.developer.domain.app;

import java.time.Instant;

public record DeveloperAppActivated(DeveloperAppId developerAppId, Instant occurredAt) implements DeveloperAppDomainEvent {}
