package et.gov.ethgov.search.domain.crawljob;

import java.util.List;
import java.util.Optional;

public interface CrawljobRepository {
    Crawljob save(Crawljob aggregate);

    Optional<Crawljob> findById(CrawljobId id);

    Optional<Crawljob> findByReferenceCode(String tenantId, String referenceCode);

    List<Crawljob> findByTenant(String tenantId, int limit);

    void delete(CrawljobId id);
}
