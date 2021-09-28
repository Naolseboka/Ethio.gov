package et.gov.ethgov.identity.application.mfa;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaId;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateMfaUseCase {
    private final MfaRepository repository;
    private final Clock clock;

    public ActivateMfaUseCase(MfaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MfaId execute(ActivateMfaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MfaId id = MfaId.parse(command.mfaId());
        Mfa aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mfa not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateMfaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mfaId,
            String reason) {}
}
