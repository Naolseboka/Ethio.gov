package et.gov.ethgov.land.application.title;

import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleRepository;
import java.util.List;
import java.util.Objects;

public final class ListTitleUseCase {
    private final TitleRepository repository;

    public ListTitleUseCase(TitleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Title> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
