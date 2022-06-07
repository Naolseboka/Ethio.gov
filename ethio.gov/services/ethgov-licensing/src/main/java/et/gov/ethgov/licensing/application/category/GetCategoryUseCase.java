package et.gov.ethgov.licensing.application.category;

import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryId;
import et.gov.ethgov.licensing.domain.category.CategoryRepository;
import java.util.Objects;

public final class GetCategoryUseCase {
    private final CategoryRepository repository;

    public GetCategoryUseCase(CategoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Category execute(String rawId) {
        CategoryId id = CategoryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("category not found: " + rawId));
    }
}
