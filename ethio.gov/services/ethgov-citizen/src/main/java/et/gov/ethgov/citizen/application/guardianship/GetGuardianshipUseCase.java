package et.gov.ethgov.citizen.application.guardianship;

import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipId;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipRepository;
import java.util.Objects;

public final class GetGuardianshipUseCase {
    private final GuardianshipRepository repository;

    public GetGuardianshipUseCase(GuardianshipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Guardianship execute(String rawId) {
        GuardianshipId id = GuardianshipId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("guardianship not found: " + rawId));
    }
}
