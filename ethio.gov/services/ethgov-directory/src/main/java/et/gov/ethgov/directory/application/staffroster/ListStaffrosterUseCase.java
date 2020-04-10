package et.gov.ethgov.directory.application.staffroster;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import java.util.List;
import java.util.Objects;

public final class ListStaffrosterUseCase {
    private final StaffrosterRepository repository;

    public ListStaffrosterUseCase(StaffrosterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Staffroster> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
