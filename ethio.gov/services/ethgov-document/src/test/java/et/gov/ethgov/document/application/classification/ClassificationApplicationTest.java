package et.gov.ethgov.document.application.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryClassificationRepository;
import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassificationApplicationTest {
    private InMemoryClassificationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryClassificationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateClassificationUseCase(repository, clock);
        var activate = new ActivateClassificationUseCase(repository, clock);
        var get = new GetClassificationUseCase(repository);

        var id = create.execute(new CreateClassificationUseCase.CreateClassificationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateClassificationUseCase.ActivateClassificationCommand(
                null, null, null, null, id.toString(), null));

        Classification loaded = get.execute(id.toString());
        assertEquals(ClassificationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
