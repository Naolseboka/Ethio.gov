package et.gov.ethgov.licensing.application.licensefee;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeId;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLicensefeeUseCase {
    private final LicensefeeRepository repository;
    private final Clock clock;

    public CreateLicensefeeUseCase(LicensefeeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LicensefeeId execute(CreateLicensefeeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Licensefee aggregate = Licensefee.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLicensefeeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String licensefeeId,
            String reason) {}
}
