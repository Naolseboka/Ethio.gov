package et.gov.ethgov.casemgmt.application.casefile;

import et.gov.ethgov.casemgmt.domain.casefile.CaseId;
import et.gov.ethgov.casemgmt.domain.casefile.CaseNumber;
import et.gov.ethgov.casemgmt.domain.casefile.CaseRepository;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCaseUseCase {

    private final CaseRepository cases;

    public GetCaseUseCase(CaseRepository cases) {
        this.cases = cases;
    }

    @Transactional(readOnly = true)
    public GovernmentCase byId(String caseId) {
        return cases.findById(CaseId.of(caseId))
                .orElseThrow(() -> new DomainException(
                        "CASE_NOT_FOUND", "Case Not Found", "Case does not exist", 404));
    }

    @Transactional(readOnly = true)
    public GovernmentCase byNumber(String tenantId, String caseNumber) {
        return cases.findByTenantAndCaseNumber(TenantId.of(tenantId), CaseNumber.of(caseNumber))
                .orElseThrow(() -> new DomainException(
                        "CASE_NOT_FOUND", "Case Not Found", "Case does not exist", 404));
    }
}
