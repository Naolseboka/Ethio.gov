package et.gov.ethgov.land.application.encumbrance;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceId;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import java.util.Objects;

public final class GetEncumbranceUseCase {
    private final EncumbranceRepository repository;

    public GetEncumbranceUseCase(EncumbranceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Encumbrance execute(String rawId) {
        EncumbranceId id = EncumbranceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("encumbrance not found: " + rawId));
    }
}
