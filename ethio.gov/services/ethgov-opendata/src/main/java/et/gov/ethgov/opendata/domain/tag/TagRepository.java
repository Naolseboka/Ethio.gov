package et.gov.ethgov.opendata.domain.tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    Tag save(Tag aggregate);

    Optional<Tag> findById(TagId id);

    Optional<Tag> findByReferenceCode(String tenantId, String referenceCode);

    List<Tag> findByTenant(String tenantId, int limit);

    void delete(TagId id);
}
