package et.gov.ethgov.licensing.domain.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category aggregate);

    Optional<Category> findById(CategoryId id);

    Optional<Category> findByReferenceCode(String tenantId, String referenceCode);

    List<Category> findByTenant(String tenantId, int limit);

    void delete(CategoryId id);
}
