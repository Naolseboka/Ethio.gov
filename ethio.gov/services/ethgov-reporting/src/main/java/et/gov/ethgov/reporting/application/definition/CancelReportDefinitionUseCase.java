package et.gov.ethgov.reporting.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.reporting.application.ReportingEventPublisher;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelReportDefinitionUseCase {

    private final ReportDefinitionRepository definitions;
    private final ReportingEventPublisher eventPublisher;
    private final Clock clock;

    public CancelReportDefinitionUseCase(
            ReportDefinitionRepository definitions, ReportingEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String reportDefinitionId, String reason) {
        Objects.requireNonNull(reportDefinitionId, "reportDefinitionId");
        ReportDefinition definition = definitions
                .findById(ReportDefinitionId.of(reportDefinitionId))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Report definition does not exist",
                        404));
        definition.cancel(reason, clock.instant());
        definitions.save(definition);
        eventPublisher.publishDefinitionEvents(definition.pullDomainEvents());
    }
}
