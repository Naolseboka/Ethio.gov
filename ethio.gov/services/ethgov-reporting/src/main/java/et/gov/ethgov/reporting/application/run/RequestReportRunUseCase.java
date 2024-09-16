package et.gov.ethgov.reporting.application.run;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import et.gov.ethgov.reporting.application.ReportingEventPublisher;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRepository;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionStatus;
import et.gov.ethgov.reporting.domain.run.ReportRun;
import et.gov.ethgov.reporting.domain.run.ReportRunId;
import et.gov.ethgov.reporting.domain.run.ReportRunRepository;
import et.gov.ethgov.reporting.domain.run.RunNumber;
import et.gov.ethgov.reporting.domain.run.RunNumberSequence;
import et.gov.ethgov.reporting.domain.run.RunParameters;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestReportRunUseCase {

    private final ReportDefinitionRepository definitions;
    private final ReportRunRepository runs;
    private final RunNumberSequence sequences;
    private final ReportingEventPublisher eventPublisher;
    private final Clock clock;

    public RequestReportRunUseCase(
            ReportDefinitionRepository definitions,
            ReportRunRepository runs,
            RunNumberSequence sequences,
            ReportingEventPublisher eventPublisher,
            Clock clock) {
        this.definitions = definitions;
        this.runs = runs;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ReportRunId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        ReportDefinition definition = definitions
                .findById(ReportDefinitionId.of(command.reportDefinitionId()))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
        if (!definition.tenantId().equals(tenantId)) {
            throw new DomainException(
                    "DEFINITION_TENANT_MISMATCH",
                    "Definition Tenant Mismatch",
                    "Report definition does not belong to the requested tenant",
                    409);
        }
        if (definition.status() != ReportDefinitionStatus.PUBLISHED) {
            throw new DomainException(
                    "DEFINITION_NOT_PUBLISHED",
                    "Definition Not Published",
                    "Only PUBLISHED report definitions can be run",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        RunNumber runNumber = RunNumber.allocate(year, sequences.nextSequence(tenantId, year));
        Instant periodStart = command.periodStart() == null ? null : Instant.parse(command.periodStart());
        Instant periodEnd = command.periodEnd() == null ? null : Instant.parse(command.periodEnd());
        ReportRun run = ReportRun.request(
                tenantId,
                runNumber,
                definition.id(),
                command.parameters() == null || command.parameters().isBlank()
                        ? RunParameters.empty()
                        : RunParameters.of(command.parameters()),
                periodStart,
                periodEnd,
                command.requestedBy(),
                clock.instant());
        runs.save(run);
        eventPublisher.publishRunEvents(run.pullDomainEvents());
        return run.id();
    }

    public record Command(
            String tenantId,
            String reportDefinitionId,
            String parameters,
            String periodStart,
            String periodEnd,
            String requestedBy) {}
}
