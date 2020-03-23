package et.gov.ethgov.directory.application.office;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeId;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendOfficeUseCase {
    private final OfficeRepository repository;
    private final Clock clock;

    public SuspendOfficeUseCase(OfficeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OfficeId execute(SuspendOfficeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OfficeId id = OfficeId.parse(command.officeId());
        Office aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("office not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendOfficeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String officeId,
            String reason) {}
}
