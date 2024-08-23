package et.gov.ethgov.reporting.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.reporting.application.ReportingEventPublisher;
import et.gov.ethgov.reporting.domain.definition.ParameterSchema;
import et.gov.ethgov.reporting.domain.definition.ReportCategory;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRepository;
import et.gov.ethgov.reporting.domain.definition.ReportFormat;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateReportDefinitionUseCase {

    private final ReportDefinitionRepository definitions;
    private final ReportingEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateReportDefinitionUseCase(
            ReportDefinitionRepository definitions, ReportingEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        ReportDefinition definition = definitions
                .findById(ReportDefinitionId.of(command.reportDefinitionId()))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
        definition.update(
                command.name(),
                command.description(),
                ReportCategory.valueOf(command.category()),
                ReportFormat.valueOf(command.format()),
                ParameterSchema.of(command.parameterSchema()),
                clock.instant());
        definitions.save(definition);
        eventPublisher.publishDefinitionEvents(definition.pullDomainEvents());
    }

    public record Command(
            String reportDefinitionId,
            String name,
            String description,
            String category,
            String format,
            String parameterSchema) {}
}
