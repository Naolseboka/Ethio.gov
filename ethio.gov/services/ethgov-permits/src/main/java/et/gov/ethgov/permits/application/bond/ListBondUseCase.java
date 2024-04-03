package et.gov.ethgov.permits.application.bond;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import java.util.List;
import java.util.Objects;

public final class ListBondUseCase {
    private final BondRepository repository;

    public ListBondUseCase(BondRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Bond> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
