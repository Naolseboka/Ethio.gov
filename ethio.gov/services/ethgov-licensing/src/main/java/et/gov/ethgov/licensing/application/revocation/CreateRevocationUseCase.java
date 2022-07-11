package et.gov.ethgov.licensing.application.revocation;

import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationId;
import et.gov.ethgov.licensing.domain.revocation.RevocationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRevocationUseCase {
    private final RevocationRepository repository;
    private final Clock clock;

    public CreateRevocationUseCase(RevocationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RevocationId execute(CreateRevocationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Revocation aggregate = Revocation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRevocationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String revocationId,
            String reason) {}
}
