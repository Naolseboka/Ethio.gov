package et.gov.ethgov.citizen.application.residency;

import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyId;
import et.gov.ethgov.citizen.domain.residency.ResidencyRepository;
import java.util.Objects;

public final class GetResidencyUseCase {
    private final ResidencyRepository repository;

    public GetResidencyUseCase(ResidencyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Residency execute(String rawId) {
        ResidencyId id = ResidencyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("residency not found: " + rawId));
    }
}
