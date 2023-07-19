package et.gov.ethgov.organization.application.mandate;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import java.util.List;
import java.util.Objects;

public final class ListMandateUseCase {
    private final MandateRepository repository;

    public ListMandateUseCase(MandateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Mandate> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
