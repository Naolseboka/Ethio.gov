package et.gov.ethgov.payments.application.settlement;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementId;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import java.util.Objects;

public final class GetSettlementUseCase {
    private final SettlementRepository repository;

    public GetSettlementUseCase(SettlementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Settlement execute(String rawId) {
        SettlementId id = SettlementId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("settlement not found: " + rawId));
    }
}
