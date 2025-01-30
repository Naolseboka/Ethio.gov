package et.gov.ethgov.revenue.application.ledger;

import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerId;
import et.gov.ethgov.revenue.domain.ledger.LedgerRepository;
import java.util.Objects;

public final class GetLedgerUseCase {
    private final LedgerRepository repository;

    public GetLedgerUseCase(LedgerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Ledger execute(String rawId) {
        LedgerId id = LedgerId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ledger not found: " + rawId));
    }
}
