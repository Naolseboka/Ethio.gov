package et.gov.ethgov.opendata.application.dataupdate;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateId;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDataupdateUseCase {
    private final DataupdateRepository repository;
    private final Clock clock;

    public CreateDataupdateUseCase(DataupdateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DataupdateId execute(CreateDataupdateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Dataupdate aggregate = Dataupdate.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDataupdateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dataupdateId,
            String reason) {}
}
