package et.gov.ethgov.citizen.application.citizenship;

import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipId;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipRepository;
import java.util.Objects;

public final class GetCitizenshipUseCase {
    private final CitizenshipRepository repository;

    public GetCitizenshipUseCase(CitizenshipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Citizenship execute(String rawId) {
        CitizenshipId id = CitizenshipId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("citizenship not found: " + rawId));
    }
}
