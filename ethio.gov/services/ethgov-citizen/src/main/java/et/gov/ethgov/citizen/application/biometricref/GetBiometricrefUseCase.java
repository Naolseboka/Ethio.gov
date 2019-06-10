package et.gov.ethgov.citizen.application.biometricref;

import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefId;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefRepository;
import java.util.Objects;

public final class GetBiometricrefUseCase {
    private final BiometricrefRepository repository;

    public GetBiometricrefUseCase(BiometricrefRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Biometricref execute(String rawId) {
        BiometricrefId id = BiometricrefId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("biometricref not found: " + rawId));
    }
}
