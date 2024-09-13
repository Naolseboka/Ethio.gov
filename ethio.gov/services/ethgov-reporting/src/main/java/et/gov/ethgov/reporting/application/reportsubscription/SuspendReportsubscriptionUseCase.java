package et.gov.ethgov.reporting.application.reportsubscription;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionId;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReportsubscriptionUseCase {
    private final ReportsubscriptionRepository repository;
    private final Clock clock;

    public SuspendReportsubscriptionUseCase(ReportsubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportsubscriptionId execute(SuspendReportsubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportsubscriptionId id = ReportsubscriptionId.parse(command.reportsubscriptionId());
        Reportsubscription aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportsubscription not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReportsubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportsubscriptionId,
            String reason) {}
}
