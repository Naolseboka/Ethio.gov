package et.gov.ethgov.case.application.appeal;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import java.util.List;
import java.util.Objects;

public final class ListAppealUseCase {
    private final AppealRepository repository;

    public ListAppealUseCase(AppealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Appeal> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
