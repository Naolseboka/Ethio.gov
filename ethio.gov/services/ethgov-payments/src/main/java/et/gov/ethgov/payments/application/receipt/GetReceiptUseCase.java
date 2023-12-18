package et.gov.ethgov.payments.application.receipt;

import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptId;
import et.gov.ethgov.payments.domain.receipt.ReceiptRepository;
import java.util.Objects;

public final class GetReceiptUseCase {
    private final ReceiptRepository repository;

    public GetReceiptUseCase(ReceiptRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Receipt execute(String rawId) {
        ReceiptId id = ReceiptId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("receipt not found: " + rawId));
    }
}
