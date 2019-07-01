package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.util.Objects;

public final class GetEmergencycontactUseCase {
    private final EmergencycontactRepository repository;

    public GetEmergencycontactUseCase(EmergencycontactRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Emergencycontact execute(String rawId) {
        EmergencycontactId id = EmergencycontactId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emergencycontact not found: " + rawId));
    }
}
