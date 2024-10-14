package et.gov.ethgov.reporting.domain.definition;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface ReportDefinitionRepository {
    boolean existsByTenantAndReportNumber(TenantId tenantId, ReportNumber reportNumber);

    boolean existsByTenantAndCode(TenantId tenantId, ReportCode code);

    Optional<ReportDefinition> findById(ReportDefinitionId id);

    Optional<ReportDefinition> findByTenantAndReportNumber(TenantId tenantId, ReportNumber reportNumber);

    Optional<ReportDefinition> findByTenantAndCode(TenantId tenantId, ReportCode code);

    void save(ReportDefinition definition);
}
