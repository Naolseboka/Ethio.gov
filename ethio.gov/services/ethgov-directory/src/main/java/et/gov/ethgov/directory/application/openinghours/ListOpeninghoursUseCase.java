package et.gov.ethgov.directory.application.openinghours;

import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursRepository;
import java.util.List;
import java.util.Objects;

public final class ListOpeninghoursUseCase {
    private final OpeninghoursRepository repository;

    public ListOpeninghoursUseCase(OpeninghoursRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Openinghours> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
