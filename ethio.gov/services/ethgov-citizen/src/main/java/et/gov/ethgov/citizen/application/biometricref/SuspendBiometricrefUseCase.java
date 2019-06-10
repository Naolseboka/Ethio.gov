package et.gov.ethgov.citizen.application.biometricref;

import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefId;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendBiometricrefUseCase {
    private final BiometricrefRepository repository;
    private final Clock clock;

    public SuspendBiometricrefUseCase(BiometricrefRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BiometricrefId execute(SuspendBiometricrefCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BiometricrefId id = BiometricrefId.parse(command.biometricrefId());
        Biometricref aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("biometricref not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendBiometricrefCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String biometricrefId,
            String reason) {}
}
