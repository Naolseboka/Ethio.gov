package et.gov.ethgov.licensing.application.suspension;

import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionId;
import et.gov.ethgov.licensing.domain.suspension.SuspensionRepository;
import java.util.Objects;

public final class GetSuspensionUseCase {
    private final SuspensionRepository repository;

    public GetSuspensionUseCase(SuspensionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Suspension execute(String rawId) {
        SuspensionId id = SuspensionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suspension not found: " + rawId));
    }
}
