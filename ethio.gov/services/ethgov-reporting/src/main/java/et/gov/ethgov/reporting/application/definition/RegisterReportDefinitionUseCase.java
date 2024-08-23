package et.gov.ethgov.reporting.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import et.gov.ethgov.reporting.application.ReportingEventPublisher;
import et.gov.ethgov.reporting.domain.definition.ParameterSchema;
import et.gov.ethgov.reporting.domain.definition.ReportCategory;
import et.gov.ethgov.reporting.domain.definition.ReportCode;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRepository;
import et.gov.ethgov.reporting.domain.definition.ReportFormat;
import et.gov.ethgov.reporting.domain.definition.ReportNumber;
import et.gov.ethgov.reporting.domain.definition.ReportNumberSequence;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterReportDefinitionUseCase {

    private final ReportDefinitionRepository definitions;
    private final ReportNumberSequence sequences;
    private final ReportingEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterReportDefinitionUseCase(
            ReportDefinitionRepository definitions,
            ReportNumberSequence sequences,
            ReportingEventPublisher eventPublisher,
            Clock clock) {
        this.definitions = definitions;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ReportDefinitionId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        ReportCode code = ReportCode.of(command.code());
        if (definitions.existsByTenantAndCode(tenantId, code)) {
            throw new DomainException(
                    "REPORT_CODE_EXISTS",
                    "Report Code Exists",
                    "A report definition with this code already exists for the tenant",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        ReportNumber reportNumber = ReportNumber.allocate(year, sequences.nextSequence(tenantId, year));
        ReportDefinition definition = ReportDefinition.register(
                tenantId,
                reportNumber,
                code,
                command.name(),
                command.description(),
                ReportCategory.valueOf(command.category()),
                ReportFormat.valueOf(command.format()),
                ParameterSchema.of(command.parameterSchema()),
                clock.instant());
        definitions.save(definition);
        eventPublisher.publishDefinitionEvents(definition.pullDomainEvents());
        return definition.id();
    }

    public record Command(
            String tenantId,
            String code,
            String name,
            String description,
            String category,
            String format,
            String parameterSchema) {}
}
