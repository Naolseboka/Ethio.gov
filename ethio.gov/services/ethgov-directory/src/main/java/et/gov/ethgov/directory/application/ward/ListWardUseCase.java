package et.gov.ethgov.directory.application.ward;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import java.util.List;
import java.util.Objects;

public final class ListWardUseCase {
    private final WardRepository repository;

    public ListWardUseCase(WardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Ward> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
