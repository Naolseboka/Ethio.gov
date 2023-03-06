package et.gov.ethgov.opendata.application.datalicense;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseId;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDatalicenseUseCase {
    private final DatalicenseRepository repository;
    private final Clock clock;

    public UpdateDatalicenseUseCase(DatalicenseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DatalicenseId execute(UpdateDatalicenseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DatalicenseId id = DatalicenseId.parse(command.datalicenseId());
        Datalicense aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("datalicense not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDatalicenseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String datalicenseId,
            String reason) {}
}
