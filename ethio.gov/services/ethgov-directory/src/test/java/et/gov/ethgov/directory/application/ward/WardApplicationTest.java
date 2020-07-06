package et.gov.ethgov.directory.application.ward;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryWardRepository;
import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WardApplicationTest {
    private InMemoryWardRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWardRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWardUseCase(repository, clock);
        var activate = new ActivateWardUseCase(repository, clock);
        var get = new GetWardUseCase(repository);

        var id = create.execute(new CreateWardUseCase.CreateWardCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWardUseCase.ActivateWardCommand(
                null, null, null, null, id.toString(), null));

        Ward loaded = get.execute(id.toString());
        assertEquals(WardStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
