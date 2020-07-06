package et.gov.ethgov.directory.application.staffroster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryStaffrosterRepository;
import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaffrosterApplicationTest {
    private InMemoryStaffrosterRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryStaffrosterRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateStaffrosterUseCase(repository, clock);
        var activate = new ActivateStaffrosterUseCase(repository, clock);
        var get = new GetStaffrosterUseCase(repository);

        var id = create.execute(new CreateStaffrosterUseCase.CreateStaffrosterCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateStaffrosterUseCase.ActivateStaffrosterCommand(
                null, null, null, null, id.toString(), null));

        Staffroster loaded = get.execute(id.toString());
        assertEquals(StaffrosterStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
