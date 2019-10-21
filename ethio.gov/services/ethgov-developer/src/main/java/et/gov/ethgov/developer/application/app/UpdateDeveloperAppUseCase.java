package et.gov.ethgov.developer.application.app;

import et.gov.ethgov.developer.application.DeveloperEventPublisher;
import et.gov.ethgov.developer.domain.app.AllowedScopes;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.app.DeveloperAppRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDeveloperAppUseCase {

    private final DeveloperAppRepository apps;
    private final DeveloperEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateDeveloperAppUseCase(
            DeveloperAppRepository apps, DeveloperEventPublisher eventPublisher, Clock clock) {
        this.apps = apps;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        DeveloperApp app = apps.findById(DeveloperAppId.of(command.developerAppId()))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
        app.update(
                command.name(),
                command.description(),
                command.ownerOrgId(),
                command.contactEmail(),
                AllowedScopes.of(command.allowedScopes()),
                clock.instant());
        apps.save(app);
        eventPublisher.publishAppEvents(app.pullDomainEvents());
    }

    public record Command(
            String developerAppId,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            String allowedScopes) {}
}
