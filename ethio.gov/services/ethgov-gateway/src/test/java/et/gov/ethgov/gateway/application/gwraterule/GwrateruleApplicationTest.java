package et.gov.ethgov.gateway.application.gwraterule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryGwrateruleRepository;
import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GwrateruleApplicationTest {
    private InMemoryGwrateruleRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGwrateruleRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateGwrateruleUseCase(repository, clock);
        var activate = new ActivateGwrateruleUseCase(repository, clock);
        var get = new GetGwrateruleUseCase(repository);

        var id = create.execute(new CreateGwrateruleUseCase.CreateGwrateruleCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateGwrateruleUseCase.ActivateGwrateruleCommand(
                null, null, null, null, id.toString(), null));

        Gwraterule loaded = get.execute(id.toString());
        assertEquals(GwrateruleStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
