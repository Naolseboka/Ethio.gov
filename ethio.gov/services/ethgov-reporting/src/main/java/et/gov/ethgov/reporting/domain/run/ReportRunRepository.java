package et.gov.ethgov.reporting.domain.run;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface ReportRunRepository {
    boolean existsByTenantAndRunNumber(TenantId tenantId, RunNumber runNumber);

    Optional<ReportRun> findById(ReportRunId id);

    Optional<ReportRun> findByTenantAndRunNumber(TenantId tenantId, RunNumber runNumber);

    void save(ReportRun run);
}
