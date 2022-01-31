package et.gov.ethgov.land.application.easement;

import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementId;
import et.gov.ethgov.land.domain.easement.EasementRepository;
import java.util.Objects;

public final class GetEasementUseCase {
    private final EasementRepository repository;

    public GetEasementUseCase(EasementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Easement execute(String rawId) {
        EasementId id = EasementId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("easement not found: " + rawId));
    }
}
