package et.gov.ethgov.identity.application.delegation;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationId;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import java.util.Objects;

public final class GetDelegationUseCase {
    private final DelegationRepository repository;

    public GetDelegationUseCase(DelegationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Delegation execute(String rawId) {
        DelegationId id = DelegationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delegation not found: " + rawId));
    }
}
