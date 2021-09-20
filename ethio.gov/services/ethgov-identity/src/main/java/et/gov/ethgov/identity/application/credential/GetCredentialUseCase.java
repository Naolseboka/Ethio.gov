package et.gov.ethgov.identity.application.credential;

import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialId;
import et.gov.ethgov.identity.domain.credential.CredentialRepository;
import java.util.Objects;

public final class GetCredentialUseCase {
    private final CredentialRepository repository;

    public GetCredentialUseCase(CredentialRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Credential execute(String rawId) {
        CredentialId id = CredentialId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("credential not found: " + rawId));
    }
}
