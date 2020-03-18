package et.gov.ethgov.directory.application.office;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeId;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOfficeUseCase {
    private final OfficeRepository repository;
    private final Clock clock;

    public CreateOfficeUseCase(OfficeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OfficeId execute(CreateOfficeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Office aggregate = Office.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOfficeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String officeId,
            String reason) {}
}
