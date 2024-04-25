package et.gov.ethgov.permits.application.permitapplication;

import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationId;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationRepository;
import java.util.Objects;

public final class GetPermitapplicationUseCase {
    private final PermitapplicationRepository repository;

    public GetPermitapplicationUseCase(PermitapplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Permitapplication execute(String rawId) {
        PermitapplicationId id = PermitapplicationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitapplication not found: " + rawId));
    }
}
