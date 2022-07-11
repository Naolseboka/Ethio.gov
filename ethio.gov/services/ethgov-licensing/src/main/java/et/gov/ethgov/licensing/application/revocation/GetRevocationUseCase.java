package et.gov.ethgov.licensing.application.revocation;

import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationId;
import et.gov.ethgov.licensing.domain.revocation.RevocationRepository;
import java.util.Objects;

public final class GetRevocationUseCase {
    private final RevocationRepository repository;

    public GetRevocationUseCase(RevocationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Revocation execute(String rawId) {
        RevocationId id = RevocationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("revocation not found: " + rawId));
    }
}
