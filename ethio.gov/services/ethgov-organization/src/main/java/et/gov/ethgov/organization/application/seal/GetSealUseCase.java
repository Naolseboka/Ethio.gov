package et.gov.ethgov.organization.application.seal;

import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealId;
import et.gov.ethgov.organization.domain.seal.SealRepository;
import java.util.Objects;

public final class GetSealUseCase {
    private final SealRepository repository;

    public GetSealUseCase(SealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Seal execute(String rawId) {
        SealId id = SealId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("seal not found: " + rawId));
    }
}
