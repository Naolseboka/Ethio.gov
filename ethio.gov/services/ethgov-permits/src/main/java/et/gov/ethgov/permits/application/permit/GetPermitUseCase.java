package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import java.util.Objects;

public final class GetPermitUseCase {
    private final PermitRepository repository;

    public GetPermitUseCase(PermitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Permit execute(String rawId) {
        PermitId id = PermitId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permit not found: " + rawId));
    }
}
