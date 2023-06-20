package et.gov.ethgov.opendata.application.apicontract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryApicontractRepository;
import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApicontractApplicationTest {
    private InMemoryApicontractRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryApicontractRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateApicontractUseCase(repository, clock);
        var activate = new ActivateApicontractUseCase(repository, clock);
        var get = new GetApicontractUseCase(repository);

        var id = create.execute(new CreateApicontractUseCase.CreateApicontractCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateApicontractUseCase.ActivateApicontractCommand(
                null, null, null, null, id.toString(), null));

        Apicontract loaded = get.execute(id.toString());
        assertEquals(ApicontractStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
