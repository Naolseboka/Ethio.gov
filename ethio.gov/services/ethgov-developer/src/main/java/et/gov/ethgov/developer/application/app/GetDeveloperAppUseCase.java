package et.gov.ethgov.developer.application.app;

import et.gov.ethgov.developer.domain.app.AppNumber;
import et.gov.ethgov.developer.domain.app.AppSlug;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.app.DeveloperAppRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDeveloperAppUseCase {

    private final DeveloperAppRepository apps;

    public GetDeveloperAppUseCase(DeveloperAppRepository apps) {
        this.apps = apps;
    }

    @Transactional(readOnly = true)
    public DeveloperApp byId(String developerAppId) {
        return apps.findById(DeveloperAppId.of(developerAppId))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
    }

    @Transactional(readOnly = true)
    public DeveloperApp byNumber(String tenantId, String appNumber) {
        return apps.findByTenantAndAppNumber(TenantId.of(tenantId), AppNumber.of(appNumber))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
    }

    @Transactional(readOnly = true)
    public DeveloperApp bySlug(String tenantId, String slug) {
        return apps.findByTenantAndSlug(TenantId.of(tenantId), AppSlug.of(slug))
                .orElseThrow(() -> new DomainException(
                        "APP_NOT_FOUND", "App Not Found", "Developer app does not exist", 404));
    }
}
