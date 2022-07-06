package et.gov.ethgov.licensing.application.licensefee;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeId;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendLicensefeeUseCase {
    private final LicensefeeRepository repository;
    private final Clock clock;

    public SuspendLicensefeeUseCase(LicensefeeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LicensefeeId execute(SuspendLicensefeeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LicensefeeId id = LicensefeeId.parse(command.licensefeeId());
        Licensefee aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("licensefee not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendLicensefeeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String licensefeeId,
            String reason) {}
}
