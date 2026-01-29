package et.gov.ethgov.workflow.application.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryDefinitionRepository;
import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefinitionApplicationTest {
    private InMemoryDefinitionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDefinitionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDefinitionUseCase(repository, clock);
        var activate = new ActivateDefinitionUseCase(repository, clock);
        var get = new GetDefinitionUseCase(repository);

        var id = create.execute(new CreateDefinitionUseCase.CreateDefinitionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDefinitionUseCase.ActivateDefinitionCommand(
                null, null, null, null, id.toString(), null));

        Definition loaded = get.execute(id.toString());
        assertEquals(DefinitionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
