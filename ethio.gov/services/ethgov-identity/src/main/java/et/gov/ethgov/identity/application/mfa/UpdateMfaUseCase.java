package et.gov.ethgov.identity.application.mfa;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaId;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateMfaUseCase {
    private final MfaRepository repository;
    private final Clock clock;

    public UpdateMfaUseCase(MfaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MfaId execute(UpdateMfaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MfaId id = MfaId.parse(command.mfaId());
        Mfa aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mfa not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateMfaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mfaId,
            String reason) {}
}
