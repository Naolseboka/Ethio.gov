package et.gov.ethgov.opendata.application.datalicense;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseId;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDatalicenseUseCase {
    private final DatalicenseRepository repository;
    private final Clock clock;

    public ActivateDatalicenseUseCase(DatalicenseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DatalicenseId execute(ActivateDatalicenseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DatalicenseId id = DatalicenseId.parse(command.datalicenseId());
        Datalicense aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("datalicense not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDatalicenseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String datalicenseId,
            String reason) {}
}
