package et.gov.ethgov.reporting.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.definition.ReportCode;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRepository;
import et.gov.ethgov.reporting.domain.definition.ReportNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetReportDefinitionUseCase {

    private final ReportDefinitionRepository definitions;

    public GetReportDefinitionUseCase(ReportDefinitionRepository definitions) {
        this.definitions = definitions;
    }

    @Transactional(readOnly = true)
    public ReportDefinition byId(String reportDefinitionId) {
        return definitions
                .findById(ReportDefinitionId.of(reportDefinitionId))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public ReportDefinition byNumber(String tenantId, String reportNumber) {
        return definitions
                .findByTenantAndReportNumber(TenantId.of(tenantId), ReportNumber.of(reportNumber))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public ReportDefinition byCode(String tenantId, String code) {
        return definitions
                .findByTenantAndCode(TenantId.of(tenantId), ReportCode.of(code))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
    }
}
