package et.gov.ethgov.directory.application.office;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import java.util.List;
import java.util.Objects;

public final class ListOfficeUseCase {
    private final OfficeRepository repository;

    public ListOfficeUseCase(OfficeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Office> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
