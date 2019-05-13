package et.gov.ethgov.casemgmt.domain.casefile;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface CaseRepository {

    boolean existsByTenantAndCaseNumber(TenantId tenantId, CaseNumber caseNumber);

    Optional<GovernmentCase> findById(CaseId id);

    Optional<GovernmentCase> findByTenantAndCaseNumber(TenantId tenantId, CaseNumber caseNumber);

    void save(GovernmentCase governmentCase);
}
