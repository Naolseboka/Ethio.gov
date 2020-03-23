package et.gov.ethgov.directory.application.office;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeId;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateOfficeUseCase {
    private final OfficeRepository repository;
    private final Clock clock;

    public UpdateOfficeUseCase(OfficeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OfficeId execute(UpdateOfficeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OfficeId id = OfficeId.parse(command.officeId());
        Office aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("office not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateOfficeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String officeId,
            String reason) {}
}
