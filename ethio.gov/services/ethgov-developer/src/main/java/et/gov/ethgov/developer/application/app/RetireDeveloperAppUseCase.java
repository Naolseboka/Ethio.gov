package et.gov.ethgov.developer.application.app;

import et.gov.ethgov.developer.application.DeveloperEventPublisher;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.app.DeveloperAppRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireDeveloperAppUseCase {

    private final DeveloperAppRepository apps;
    private final DeveloperEventPublisher eventPublisher;
    private final Clock clock;

    public RetireDeveloperAppUseCase(
            DeveloperAppRepository apps, DeveloperEventPublisher eventPublisher, Clock clock) {
        this.apps = apps;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String developerAppId) {
        Objects.requireNonNull(developerAppId, "developerAppId");
        DeveloperApp app = apps.findById(DeveloperAppId.of(developerAppId))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
        app.retire(clock.instant());
        apps.save(app);
        eventPublisher.publishAppEvents(app.pullDomainEvents());
    }
}
