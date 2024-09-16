package et.gov.ethgov.reporting.application.run;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.run.ReportRun;
import et.gov.ethgov.reporting.domain.run.ReportRunId;
import et.gov.ethgov.reporting.domain.run.ReportRunRepository;
import et.gov.ethgov.reporting.domain.run.RunNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetReportRunUseCase {

    private final ReportRunRepository runs;

    public GetReportRunUseCase(ReportRunRepository runs) {
        this.runs = runs;
    }

    @Transactional(readOnly = true)
    public ReportRun byId(String reportRunId) {
        return runs.findById(ReportRunId.of(reportRunId))
                .orElseThrow(() -> new DomainException(
                        "RUN_NOT_FOUND", "Run Not Found", "Report run does not exist", 404));
    }

    @Transactional(readOnly = true)
    public ReportRun byNumber(String tenantId, String runNumber) {
        return runs.findByTenantAndRunNumber(TenantId.of(tenantId), RunNumber.of(runNumber))
                .orElseThrow(() -> new DomainException(
                        "RUN_NOT_FOUND", "Run Not Found", "Report run does not exist", 404));
    }
}
