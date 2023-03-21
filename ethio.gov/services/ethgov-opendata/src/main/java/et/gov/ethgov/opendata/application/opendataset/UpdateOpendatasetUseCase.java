package et.gov.ethgov.opendata.application.opendataset;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetId;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateOpendatasetUseCase {
    private final OpendatasetRepository repository;
    private final Clock clock;

    public UpdateOpendatasetUseCase(OpendatasetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OpendatasetId execute(UpdateOpendatasetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OpendatasetId id = OpendatasetId.parse(command.opendatasetId());
        Opendataset aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("opendataset not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateOpendatasetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String opendatasetId,
            String reason) {}
}
