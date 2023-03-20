package et.gov.ethgov.opendata.application.opendataset;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetId;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateOpendatasetUseCase {
    private final OpendatasetRepository repository;
    private final Clock clock;

    public ActivateOpendatasetUseCase(OpendatasetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OpendatasetId execute(ActivateOpendatasetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OpendatasetId id = OpendatasetId.parse(command.opendatasetId());
        Opendataset aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("opendataset not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateOpendatasetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String opendatasetId,
            String reason) {}
}
