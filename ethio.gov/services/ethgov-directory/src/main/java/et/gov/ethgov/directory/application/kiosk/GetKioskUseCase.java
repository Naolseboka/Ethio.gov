package et.gov.ethgov.directory.application.kiosk;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskId;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import java.util.Objects;

public final class GetKioskUseCase {
    private final KioskRepository repository;

    public GetKioskUseCase(KioskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Kiosk execute(String rawId) {
        KioskId id = KioskId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("kiosk not found: " + rawId));
    }
}
