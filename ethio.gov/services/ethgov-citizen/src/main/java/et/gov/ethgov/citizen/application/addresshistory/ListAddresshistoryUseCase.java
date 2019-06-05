package et.gov.ethgov.citizen.application.addresshistory;

import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryRepository;
import java.util.List;
import java.util.Objects;

public final class ListAddresshistoryUseCase {
    private final AddresshistoryRepository repository;

    public ListAddresshistoryUseCase(AddresshistoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Addresshistory> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
