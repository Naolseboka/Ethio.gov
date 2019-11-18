package et.gov.ethgov.developer.application.portaluser;

import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserRepository;
import java.util.List;
import java.util.Objects;

public final class ListPortaluserUseCase {
    private final PortaluserRepository repository;

    public ListPortaluserUseCase(PortaluserRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Portaluser> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
