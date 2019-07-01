package et.gov.ethgov.citizen.application.emergencycontact;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import java.util.List;
import java.util.Objects;

public final class ListEmergencycontactUseCase {
    private final EmergencycontactRepository repository;

    public ListEmergencycontactUseCase(EmergencycontactRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Emergencycontact> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
