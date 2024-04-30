package et.gov.ethgov.permits.application.permitchecklist;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistId;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import java.util.Objects;

public final class GetPermitchecklistUseCase {
    private final PermitchecklistRepository repository;

    public GetPermitchecklistUseCase(PermitchecklistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Permitchecklist execute(String rawId) {
        PermitchecklistId id = PermitchecklistId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitchecklist not found: " + rawId));
    }
}
