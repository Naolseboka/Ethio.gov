package et.gov.ethgov.identity.application.passwordpolicy;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyId;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import java.util.Objects;

public final class GetPasswordpolicyUseCase {
    private final PasswordpolicyRepository repository;

    public GetPasswordpolicyUseCase(PasswordpolicyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Passwordpolicy execute(String rawId) {
        PasswordpolicyId id = PasswordpolicyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("passwordpolicy not found: " + rawId));
    }
}
