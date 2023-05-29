package et.gov.ethgov.opendata.domain.publisher;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository {
    Publisher save(Publisher aggregate);

    Optional<Publisher> findById(PublisherId id);

    Optional<Publisher> findByReferenceCode(String tenantId, String referenceCode);

    List<Publisher> findByTenant(String tenantId, int limit);

    void delete(PublisherId id);
}
