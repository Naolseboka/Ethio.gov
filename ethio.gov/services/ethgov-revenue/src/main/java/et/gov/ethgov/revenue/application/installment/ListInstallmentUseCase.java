package et.gov.ethgov.revenue.application.installment;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListInstallmentUseCase {
    private final InstallmentRepository repository;

    public ListInstallmentUseCase(InstallmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Installment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
