package et.gov.ethgov.licensing.application.licensefee;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeId;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateLicensefeeUseCase {
    private final LicensefeeRepository repository;
    private final Clock clock;

    public ActivateLicensefeeUseCase(LicensefeeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LicensefeeId execute(ActivateLicensefeeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LicensefeeId id = LicensefeeId.parse(command.licensefeeId());
        Licensefee aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("licensefee not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateLicensefeeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String licensefeeId,
            String reason) {}
}
