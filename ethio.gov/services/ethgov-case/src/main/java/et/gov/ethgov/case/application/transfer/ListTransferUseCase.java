package et.gov.ethgov.case.application.transfer;

import et.gov.ethgov.case.domain.transfer.Transfer;
import et.gov.ethgov.case.domain.transfer.TransferRepository;
import java.util.List;
import java.util.Objects;

public final class ListTransferUseCase {
    private final TransferRepository repository;

    public ListTransferUseCase(TransferRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Transfer> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
