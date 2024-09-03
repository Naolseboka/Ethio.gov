package et.gov.ethgov.reporting.application.reportalert;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertId;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReportalertUseCase {
    private final ReportalertRepository repository;
    private final Clock clock;

    public CreateReportalertUseCase(ReportalertRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportalertId execute(CreateReportalertCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Reportalert aggregate = Reportalert.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReportalertCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportalertId,
            String reason) {}
}
