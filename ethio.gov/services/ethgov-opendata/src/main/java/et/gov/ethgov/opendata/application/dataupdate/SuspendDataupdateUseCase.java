package et.gov.ethgov.opendata.application.dataupdate;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateId;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDataupdateUseCase {
    private final DataupdateRepository repository;
    private final Clock clock;

    public SuspendDataupdateUseCase(DataupdateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DataupdateId execute(SuspendDataupdateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DataupdateId id = DataupdateId.parse(command.dataupdateId());
        Dataupdate aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dataupdate not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDataupdateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dataupdateId,
            String reason) {}
}
