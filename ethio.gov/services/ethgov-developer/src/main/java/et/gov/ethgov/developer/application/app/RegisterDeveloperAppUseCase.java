package et.gov.ethgov.developer.application.app;

import et.gov.ethgov.developer.application.DeveloperEventPublisher;
import et.gov.ethgov.developer.domain.app.AllowedScopes;
import et.gov.ethgov.developer.domain.app.AppNumber;
import et.gov.ethgov.developer.domain.app.AppNumberSequence;
import et.gov.ethgov.developer.domain.app.AppSlug;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.app.DeveloperAppRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterDeveloperAppUseCase {

    private final DeveloperAppRepository apps;
    private final AppNumberSequence sequences;
    private final DeveloperEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterDeveloperAppUseCase(
            DeveloperAppRepository apps,
            AppNumberSequence sequences,
            DeveloperEventPublisher eventPublisher,
            Clock clock) {
        this.apps = apps;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public DeveloperAppId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        AppSlug slug = AppSlug.of(command.slug());
        if (apps.existsByTenantAndSlug(tenantId, slug)) {
            throw new DomainException(
                    "APP_SLUG_EXISTS",
                    "App Slug Exists",
                    "A developer app with this slug already exists for the tenant",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        AppNumber appNumber = AppNumber.allocate(year, sequences.nextSequence(tenantId, year));
        DeveloperApp app = DeveloperApp.register(
                tenantId,
                appNumber,
                slug,
                command.name(),
                command.description(),
                command.ownerOrgId(),
                command.contactEmail(),
                AllowedScopes.of(command.allowedScopes()),
                clock.instant());
        apps.save(app);
        eventPublisher.publishAppEvents(app.pullDomainEvents());
        return app.id();
    }

    public record Command(
            String tenantId,
            String slug,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            String allowedScopes) {}
}
