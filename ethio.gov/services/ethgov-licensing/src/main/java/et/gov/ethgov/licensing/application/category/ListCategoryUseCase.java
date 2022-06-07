package et.gov.ethgov.licensing.application.category;

import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryRepository;
import java.util.List;
import java.util.Objects;

public final class ListCategoryUseCase {
    private final CategoryRepository repository;

    public ListCategoryUseCase(CategoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Category> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
