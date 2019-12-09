package et.gov.ethgov.developer.domain.app;

import java.time.Instant;

public record DeveloperAppCancelled(DeveloperAppId developerAppId, String reason, Instant occurredAt) implements DeveloperAppDomainEvent {}
