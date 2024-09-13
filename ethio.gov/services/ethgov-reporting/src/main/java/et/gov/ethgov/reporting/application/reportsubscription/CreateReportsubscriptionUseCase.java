package et.gov.ethgov.reporting.application.reportsubscription;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionId;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReportsubscriptionUseCase {
    private final ReportsubscriptionRepository repository;
    private final Clock clock;

    public CreateReportsubscriptionUseCase(ReportsubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportsubscriptionId execute(CreateReportsubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Reportsubscription aggregate = Reportsubscription.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReportsubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportsubscriptionId,
            String reason) {}
}
