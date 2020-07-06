package et.gov.ethgov.directory.application.regionnode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryRegionnodeRepository;
import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegionnodeApplicationTest {
    private InMemoryRegionnodeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRegionnodeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRegionnodeUseCase(repository, clock);
        var activate = new ActivateRegionnodeUseCase(repository, clock);
        var get = new GetRegionnodeUseCase(repository);

        var id = create.execute(new CreateRegionnodeUseCase.CreateRegionnodeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRegionnodeUseCase.ActivateRegionnodeCommand(
                null, null, null, null, id.toString(), null));

        Regionnode loaded = get.execute(id.toString());
        assertEquals(RegionnodeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
