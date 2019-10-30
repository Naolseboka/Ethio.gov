package et.gov.ethgov.developer.application.devcredential;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import java.util.List;
import java.util.Objects;

public final class ListDevcredentialUseCase {
    private final DevcredentialRepository repository;

    public ListDevcredentialUseCase(DevcredentialRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Devcredential> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
