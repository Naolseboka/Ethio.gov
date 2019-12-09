package et.gov.ethgov.developer.domain.app;

import java.time.Instant;

public sealed interface DeveloperAppDomainEvent
        permits DeveloperAppRegistered,
                DeveloperAppUpdated,
                DeveloperAppActivated,
                DeveloperAppSuspended,
                DeveloperAppRetired,
                DeveloperAppCancelled {
    DeveloperAppId developerAppId();

    Instant occurredAt();
}
