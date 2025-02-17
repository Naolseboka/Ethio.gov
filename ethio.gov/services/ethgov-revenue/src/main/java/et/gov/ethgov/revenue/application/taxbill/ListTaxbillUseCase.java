package et.gov.ethgov.revenue.application.taxbill;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import java.util.List;
import java.util.Objects;

public final class ListTaxbillUseCase {
    private final TaxbillRepository repository;

    public ListTaxbillUseCase(TaxbillRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Taxbill> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
