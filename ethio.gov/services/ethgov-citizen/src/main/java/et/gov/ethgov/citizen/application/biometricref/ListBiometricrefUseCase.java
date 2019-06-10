package et.gov.ethgov.citizen.application.biometricref;

import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefRepository;
import java.util.List;
import java.util.Objects;

public final class ListBiometricrefUseCase {
    private final BiometricrefRepository repository;

    public ListBiometricrefUseCase(BiometricrefRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Biometricref> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
