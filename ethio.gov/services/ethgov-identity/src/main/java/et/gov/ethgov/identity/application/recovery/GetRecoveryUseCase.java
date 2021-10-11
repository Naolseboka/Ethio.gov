package et.gov.ethgov.identity.application.recovery;

import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryId;
import et.gov.ethgov.identity.domain.recovery.RecoveryRepository;
import java.util.Objects;

public final class GetRecoveryUseCase {
    private final RecoveryRepository repository;

    public GetRecoveryUseCase(RecoveryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Recovery execute(String rawId) {
        RecoveryId id = RecoveryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("recovery not found: " + rawId));
    }
}
