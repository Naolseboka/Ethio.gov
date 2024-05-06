package et.gov.ethgov.permits.application.permitcondition;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionId;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import java.util.Objects;

public final class GetPermitconditionUseCase {
    private final PermitconditionRepository repository;

    public GetPermitconditionUseCase(PermitconditionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Permitcondition execute(String rawId) {
        PermitconditionId id = PermitconditionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitcondition not found: " + rawId));
    }
}
