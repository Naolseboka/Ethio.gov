package et.gov.ethgov.gateway.application.cors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryCorsRepository;
import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorsApplicationTest {
    private InMemoryCorsRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCorsRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCorsUseCase(repository, clock);
        var activate = new ActivateCorsUseCase(repository, clock);
        var get = new GetCorsUseCase(repository);

        var id = create.execute(new CreateCorsUseCase.CreateCorsCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCorsUseCase.ActivateCorsCommand(
                null, null, null, null, id.toString(), null));

        Cors loaded = get.execute(id.toString());
        assertEquals(CorsStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
