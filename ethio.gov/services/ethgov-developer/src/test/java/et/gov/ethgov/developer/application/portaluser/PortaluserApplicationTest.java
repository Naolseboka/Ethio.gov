package et.gov.ethgov.developer.application.portaluser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryPortaluserRepository;
import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortaluserApplicationTest {
    private InMemoryPortaluserRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPortaluserRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePortaluserUseCase(repository, clock);
        var activate = new ActivatePortaluserUseCase(repository, clock);
        var get = new GetPortaluserUseCase(repository);

        var id = create.execute(new CreatePortaluserUseCase.CreatePortaluserCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePortaluserUseCase.ActivatePortaluserCommand(
                null, null, null, null, id.toString(), null));

        Portaluser loaded = get.execute(id.toString());
        assertEquals(PortaluserStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
