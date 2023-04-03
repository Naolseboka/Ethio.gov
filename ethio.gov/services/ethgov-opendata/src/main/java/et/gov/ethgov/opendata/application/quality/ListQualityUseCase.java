package et.gov.ethgov.opendata.application.quality;

import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityRepository;
import java.util.List;
import java.util.Objects;

public final class ListQualityUseCase {
    private final QualityRepository repository;

    public ListQualityUseCase(QualityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Quality> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
