package et.gov.ethgov.opendata.application.datalicense;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseId;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDatalicenseUseCase {
    private final DatalicenseRepository repository;
    private final Clock clock;

    public CreateDatalicenseUseCase(DatalicenseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DatalicenseId execute(CreateDatalicenseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Datalicense aggregate = Datalicense.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDatalicenseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String datalicenseId,
            String reason) {}
}
