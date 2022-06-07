package et.gov.ethgov.licensing.application.compliance;

import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceRepository;
import java.util.List;
import java.util.Objects;

public final class ListComplianceUseCase {
    private final ComplianceRepository repository;

    public ListComplianceUseCase(ComplianceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Compliance> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
