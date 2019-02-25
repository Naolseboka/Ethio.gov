package et.gov.ethgov.case.application.transfer;

import et.gov.ethgov.case.domain.transfer.Transfer;
import et.gov.ethgov.case.domain.transfer.TransferId;
import et.gov.ethgov.case.domain.transfer.TransferRepository;
import java.util.Objects;

public final class GetTransferUseCase {
    private final TransferRepository repository;

    public GetTransferUseCase(TransferRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Transfer execute(String rawId) {
        TransferId id = TransferId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("transfer not found: " + rawId));
    }
}
