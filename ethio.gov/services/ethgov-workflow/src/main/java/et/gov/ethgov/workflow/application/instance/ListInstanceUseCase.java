package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceRepository;
import java.util.List;
import java.util.Objects;

public final class ListInstanceUseCase {
    private final InstanceRepository repository;

    public ListInstanceUseCase(InstanceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Instance> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
