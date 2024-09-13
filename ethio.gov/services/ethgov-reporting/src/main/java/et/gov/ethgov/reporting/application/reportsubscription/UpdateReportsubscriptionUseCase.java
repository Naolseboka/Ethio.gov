package et.gov.ethgov.reporting.application.reportsubscription;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionId;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateReportsubscriptionUseCase {
    private final ReportsubscriptionRepository repository;
    private final Clock clock;

    public UpdateReportsubscriptionUseCase(ReportsubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportsubscriptionId execute(UpdateReportsubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportsubscriptionId id = ReportsubscriptionId.parse(command.reportsubscriptionId());
        Reportsubscription aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportsubscription not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateReportsubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportsubscriptionId,
            String reason) {}
}
