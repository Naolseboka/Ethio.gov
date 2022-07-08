package et.gov.ethgov.licensing.application.renewal;

import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalRepository;
import java.util.List;
import java.util.Objects;

public final class ListRenewalUseCase {
    private final RenewalRepository repository;

    public ListRenewalUseCase(RenewalRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Renewal> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
