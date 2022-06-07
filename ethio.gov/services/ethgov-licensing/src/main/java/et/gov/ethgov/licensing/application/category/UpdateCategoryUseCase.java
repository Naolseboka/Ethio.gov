package et.gov.ethgov.licensing.application.category;

import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryId;
import et.gov.ethgov.licensing.domain.category.CategoryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCategoryUseCase {
    private final CategoryRepository repository;
    private final Clock clock;

    public UpdateCategoryUseCase(CategoryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CategoryId execute(UpdateCategoryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CategoryId id = CategoryId.parse(command.categoryId());
        Category aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("category not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCategoryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String categoryId,
            String reason) {}
}
