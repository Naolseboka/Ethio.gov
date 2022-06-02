package et.gov.ethgov.licensing.application.category;

import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryId;
import et.gov.ethgov.licensing.domain.category.CategoryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCategoryUseCase {
    private final CategoryRepository repository;
    private final Clock clock;

    public CreateCategoryUseCase(CategoryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CategoryId execute(CreateCategoryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Category aggregate = Category.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCategoryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String categoryId,
            String reason) {}
}
