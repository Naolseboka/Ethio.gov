package et.gov.ethgov.opendata.application.publisher;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import java.util.List;
import java.util.Objects;

public final class ListPublisherUseCase {
    private final PublisherRepository repository;

    public ListPublisherUseCase(PublisherRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Publisher> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
