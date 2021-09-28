package et.gov.ethgov.identity.application.mfa;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaId;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import java.util.Objects;

public final class GetMfaUseCase {
    private final MfaRepository repository;

    public GetMfaUseCase(MfaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Mfa execute(String rawId) {
        MfaId id = MfaId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mfa not found: " + rawId));
    }
}
