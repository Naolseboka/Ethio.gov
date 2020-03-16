package et.gov.ethgov.directory.application.kiosk;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import java.util.List;
import java.util.Objects;

public final class ListKioskUseCase {
    private final KioskRepository repository;

    public ListKioskUseCase(KioskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Kiosk> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
