package et.gov.ethgov.citizen.application.vitalevent;

import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventRepository;
import java.util.List;
import java.util.Objects;

public final class ListVitaleventUseCase {
    private final VitaleventRepository repository;

    public ListVitaleventUseCase(VitaleventRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Vitalevent> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
