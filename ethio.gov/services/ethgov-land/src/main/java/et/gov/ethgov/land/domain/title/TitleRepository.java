package et.gov.ethgov.land.domain.title;

import java.util.List;
import java.util.Optional;

public interface TitleRepository {
    Title save(Title aggregate);

    Optional<Title> findById(TitleId id);

    Optional<Title> findByReferenceCode(String tenantId, String referenceCode);

    List<Title> findByTenant(String tenantId, int limit);

    void delete(TitleId id);
}
