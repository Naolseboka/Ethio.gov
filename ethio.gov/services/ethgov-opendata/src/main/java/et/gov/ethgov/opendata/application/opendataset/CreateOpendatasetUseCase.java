package et.gov.ethgov.opendata.application.opendataset;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetId;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOpendatasetUseCase {
    private final OpendatasetRepository repository;
    private final Clock clock;

    public CreateOpendatasetUseCase(OpendatasetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OpendatasetId execute(CreateOpendatasetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Opendataset aggregate = Opendataset.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOpendatasetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String opendatasetId,
            String reason) {}
}
