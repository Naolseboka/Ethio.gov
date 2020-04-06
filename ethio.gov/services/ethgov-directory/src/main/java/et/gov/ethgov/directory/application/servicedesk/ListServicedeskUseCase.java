package et.gov.ethgov.directory.application.servicedesk;

import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskRepository;
import java.util.List;
import java.util.Objects;

public final class ListServicedeskUseCase {
    private final ServicedeskRepository repository;

    public ListServicedeskUseCase(ServicedeskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Servicedesk> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
