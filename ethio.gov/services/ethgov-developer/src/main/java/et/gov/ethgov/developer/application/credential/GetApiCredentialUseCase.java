package et.gov.ethgov.developer.application.credential;

import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.credential.ApiCredential;
import et.gov.ethgov.developer.domain.credential.ApiCredentialId;
import et.gov.ethgov.developer.domain.credential.ApiCredentialRepository;
import et.gov.ethgov.developer.domain.credential.CredentialNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetApiCredentialUseCase {

    private final ApiCredentialRepository credentials;

    public GetApiCredentialUseCase(ApiCredentialRepository credentials) {
        this.credentials = credentials;
    }

    @Transactional(readOnly = true)
    public ApiCredential byId(String apiCredentialId) {
        return credentials
                .findById(ApiCredentialId.of(apiCredentialId))
                .orElseThrow(() -> new DomainException(
                        "CREDENTIAL_NOT_FOUND",
                        "Credential Not Found",
                        "API credential does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public ApiCredential byNumber(String tenantId, String credentialNumber) {
        return credentials
                .findByTenantAndCredentialNumber(
                        TenantId.of(tenantId), CredentialNumber.of(credentialNumber))
                .orElseThrow(() -> new DomainException(
                        "CREDENTIAL_NOT_FOUND",
                        "Credential Not Found",
                        "API credential does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public List<ApiCredential> byApp(String developerAppId) {
        return credentials.findByAppId(DeveloperAppId.of(developerAppId));
    }
}
