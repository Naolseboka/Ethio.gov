package et.gov.ethgov.opendata.application.dataupdate;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateId;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDataupdateUseCase {
    private final DataupdateRepository repository;
    private final Clock clock;

    public ActivateDataupdateUseCase(DataupdateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DataupdateId execute(ActivateDataupdateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DataupdateId id = DataupdateId.parse(command.dataupdateId());
        Dataupdate aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dataupdate not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDataupdateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dataupdateId,
            String reason) {}
}
