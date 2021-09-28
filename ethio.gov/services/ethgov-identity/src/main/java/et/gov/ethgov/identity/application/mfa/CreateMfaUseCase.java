package et.gov.ethgov.identity.application.mfa;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaId;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMfaUseCase {
    private final MfaRepository repository;
    private final Clock clock;

    public CreateMfaUseCase(MfaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MfaId execute(CreateMfaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Mfa aggregate = Mfa.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMfaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mfaId,
            String reason) {}
}
