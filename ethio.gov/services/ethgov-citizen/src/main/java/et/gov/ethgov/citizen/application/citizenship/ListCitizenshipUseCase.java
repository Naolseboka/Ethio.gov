package et.gov.ethgov.citizen.application.citizenship;

import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipRepository;
import java.util.List;
import java.util.Objects;

public final class ListCitizenshipUseCase {
    private final CitizenshipRepository repository;

    public ListCitizenshipUseCase(CitizenshipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Citizenship> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
