package et.gov.ethgov.revenue.application.taxbill;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillId;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import java.util.Objects;

public final class GetTaxbillUseCase {
    private final TaxbillRepository repository;

    public GetTaxbillUseCase(TaxbillRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Taxbill execute(String rawId) {
        TaxbillId id = TaxbillId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("taxbill not found: " + rawId));
    }
}
