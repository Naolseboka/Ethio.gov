package et.gov.ethgov.developer.application.credential;

import et.gov.ethgov.developer.application.DeveloperEventPublisher;
import et.gov.ethgov.developer.domain.credential.ApiCredential;
import et.gov.ethgov.developer.domain.credential.ApiCredentialId;
import et.gov.ethgov.developer.domain.credential.ApiCredentialRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RevokeApiCredentialUseCase {

    private final ApiCredentialRepository credentials;
    private final DeveloperEventPublisher eventPublisher;
    private final Clock clock;

    public RevokeApiCredentialUseCase(
            ApiCredentialRepository credentials, DeveloperEventPublisher eventPublisher, Clock clock) {
        this.credentials = credentials;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String apiCredentialId, String reason) {
        Objects.requireNonNull(apiCredentialId, "apiCredentialId");
        ApiCredential credential = credentials
                .findById(ApiCredentialId.of(apiCredentialId))
                .orElseThrow(() -> new DomainException(
                        "CREDENTIAL_NOT_FOUND",
                        "Credential Not Found",
                        "API credential does not exist",
                        404));
        credential.revoke(reason, clock.instant());
        credentials.save(credential);
        eventPublisher.publishCredentialEvents(credential.pullDomainEvents());
    }
}
