package et.gov.ethgov.identity.application.lockout;

import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutId;
import et.gov.ethgov.identity.domain.lockout.LockoutRepository;
import java.util.Objects;

public final class GetLockoutUseCase {
    private final LockoutRepository repository;

    public GetLockoutUseCase(LockoutRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Lockout execute(String rawId) {
        LockoutId id = LockoutId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lockout not found: " + rawId));
    }
}
