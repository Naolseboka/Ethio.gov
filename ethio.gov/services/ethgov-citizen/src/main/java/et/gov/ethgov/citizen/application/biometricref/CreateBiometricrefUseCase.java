package et.gov.ethgov.citizen.application.biometricref;

import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefId;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBiometricrefUseCase {
    private final BiometricrefRepository repository;
    private final Clock clock;

    public CreateBiometricrefUseCase(BiometricrefRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BiometricrefId execute(CreateBiometricrefCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Biometricref aggregate = Biometricref.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBiometricrefCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String biometricrefId,
            String reason) {}
}
