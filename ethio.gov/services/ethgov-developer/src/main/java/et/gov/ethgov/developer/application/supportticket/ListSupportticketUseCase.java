package et.gov.ethgov.developer.application.supportticket;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import java.util.List;
import java.util.Objects;

public final class ListSupportticketUseCase {
    private final SupportticketRepository repository;

    public ListSupportticketUseCase(SupportticketRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Supportticket> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
