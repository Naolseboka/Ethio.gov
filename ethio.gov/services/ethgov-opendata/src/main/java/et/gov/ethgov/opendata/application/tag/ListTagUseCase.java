package et.gov.ethgov.opendata.application.tag;

import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagRepository;
import java.util.List;
import java.util.Objects;

public final class ListTagUseCase {
    private final TagRepository repository;

    public ListTagUseCase(TagRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Tag> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
