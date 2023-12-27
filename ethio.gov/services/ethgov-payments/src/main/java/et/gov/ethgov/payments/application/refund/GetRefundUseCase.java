package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.util.Objects;

public final class GetRefundUseCase {
    private final RefundRepository repository;

    public GetRefundUseCase(RefundRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Refund execute(String rawId) {
        RefundId id = RefundId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("refund not found: " + rawId));
    }
}
