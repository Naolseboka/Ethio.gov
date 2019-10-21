package et.gov.ethgov.developer.application.credential;

import et.gov.ethgov.developer.application.DeveloperEventPublisher;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.app.DeveloperAppRepository;
import et.gov.ethgov.developer.domain.app.DeveloperAppStatus;
import et.gov.ethgov.developer.domain.credential.ApiCredential;
import et.gov.ethgov.developer.domain.credential.ApiCredentialRepository;
import et.gov.ethgov.developer.domain.credential.CredentialNumber;
import et.gov.ethgov.developer.domain.credential.CredentialNumberSequence;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueApiCredentialUseCase {

    private final DeveloperAppRepository apps;
    private final ApiCredentialRepository credentials;
    private final CredentialNumberSequence sequences;
    private final DeveloperEventPublisher eventPublisher;
    private final Clock clock;

    public IssueApiCredentialUseCase(
            DeveloperAppRepository apps,
            ApiCredentialRepository credentials,
            CredentialNumberSequence sequences,
            DeveloperEventPublisher eventPublisher,
            Clock clock) {
        this.apps = apps;
        this.credentials = credentials;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public Result execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        DeveloperApp app = apps.findById(DeveloperAppId.of(command.developerAppId()))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
        if (!app.tenantId().equals(tenantId)) {
            throw new DomainException(
                    "APP_TENANT_MISMATCH",
                    "App Tenant Mismatch",
                    "Developer app does not belong to the requested tenant",
                    409);
        }
        if (app.status() != DeveloperAppStatus.ACTIVE) {
            throw new DomainException(
                    "APP_NOT_ACTIVE",
                    "App Not Active",
                    "API credentials can only be issued for ACTIVE apps",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        CredentialNumber credentialNumber =
                CredentialNumber.allocate(year, sequences.nextSequence(tenantId, year));
        ApiKeyHasher.GeneratedKey generated = ApiKeyHasher.generate();
        Instant expiresAt =
                command.expiresAt() == null || command.expiresAt().isBlank()
                        ? null
                        : Instant.parse(command.expiresAt());
        ApiCredential credential = ApiCredential.issue(
                tenantId,
                credentialNumber,
                app.id(),
                generated.prefix(),
                generated.hash(),
                command.label(),
                expiresAt,
                clock.instant());
        credentials.save(credential);
        eventPublisher.publishCredentialEvents(credential.pullDomainEvents());
        return new Result(credential.id().value(), generated.secret(), generated.prefix());
    }

    public record Command(
            String tenantId, String developerAppId, String label, String expiresAt) {}

    public record Result(String apiCredentialId, String apiKey, String keyPrefix) {}
}
