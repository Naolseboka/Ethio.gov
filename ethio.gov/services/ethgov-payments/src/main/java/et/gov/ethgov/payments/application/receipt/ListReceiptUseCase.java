package et.gov.ethgov.payments.application.receipt;

import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptRepository;
import java.util.List;
import java.util.Objects;

public final class ListReceiptUseCase {
    private final ReceiptRepository repository;

    public ListReceiptUseCase(ReceiptRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Receipt> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
