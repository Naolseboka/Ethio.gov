package et.gov.ethgov.land.application.conflict;

import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictId;
import et.gov.ethgov.land.domain.conflict.ConflictRepository;
import java.util.Objects;

public final class GetConflictUseCase {
    private final ConflictRepository repository;

    public GetConflictUseCase(ConflictRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Conflict execute(String rawId) {
        ConflictId id = ConflictId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("conflict not found: " + rawId));
    }
}
