package et.gov.ethgov.licensing.application.revocation;

import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationId;
import et.gov.ethgov.licensing.domain.revocation.RevocationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRevocationUseCase {
    private final RevocationRepository repository;
    private final Clock clock;

    public UpdateRevocationUseCase(RevocationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RevocationId execute(UpdateRevocationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RevocationId id = RevocationId.parse(command.revocationId());
        Revocation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("revocation not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRevocationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String revocationId,
            String reason) {}
}
