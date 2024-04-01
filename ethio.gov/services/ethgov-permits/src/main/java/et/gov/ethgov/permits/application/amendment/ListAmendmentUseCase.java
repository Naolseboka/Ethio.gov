package et.gov.ethgov.permits.application.amendment;

import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListAmendmentUseCase {
    private final AmendmentRepository repository;

    public ListAmendmentUseCase(AmendmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Amendment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
