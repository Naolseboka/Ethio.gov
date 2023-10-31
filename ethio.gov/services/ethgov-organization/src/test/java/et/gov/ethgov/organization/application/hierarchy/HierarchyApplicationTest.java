package et.gov.ethgov.organization.application.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryHierarchyRepository;
import et.gov.ethgov.organization.domain.hierarchy.Hierarchy;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HierarchyApplicationTest {
    private InMemoryHierarchyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHierarchyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHierarchyUseCase(repository, clock);
        var activate = new ActivateHierarchyUseCase(repository, clock);
        var get = new GetHierarchyUseCase(repository);

        var id = create.execute(new CreateHierarchyUseCase.CreateHierarchyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHierarchyUseCase.ActivateHierarchyCommand(
                null, null, null, null, id.toString(), null));

        Hierarchy loaded = get.execute(id.toString());
        assertEquals(HierarchyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
