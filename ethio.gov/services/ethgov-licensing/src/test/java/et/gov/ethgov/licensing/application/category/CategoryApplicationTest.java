package et.gov.ethgov.licensing.application.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryCategoryRepository;
import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryApplicationTest {
    private InMemoryCategoryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCategoryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCategoryUseCase(repository, clock);
        var activate = new ActivateCategoryUseCase(repository, clock);
        var get = new GetCategoryUseCase(repository);

        var id = create.execute(new CreateCategoryUseCase.CreateCategoryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCategoryUseCase.ActivateCategoryCommand(
                null, null, null, null, id.toString(), null));

        Category loaded = get.execute(id.toString());
        assertEquals(CategoryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
