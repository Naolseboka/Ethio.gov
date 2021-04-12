package et.gov.ethgov.gateway.application.circuitbreaker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryCircuitbreakerRepository;
import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircuitbreakerApplicationTest {
    private InMemoryCircuitbreakerRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCircuitbreakerRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCircuitbreakerUseCase(repository, clock);
        var activate = new ActivateCircuitbreakerUseCase(repository, clock);
        var get = new GetCircuitbreakerUseCase(repository);

        var id = create.execute(new CreateCircuitbreakerUseCase.CreateCircuitbreakerCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCircuitbreakerUseCase.ActivateCircuitbreakerCommand(
                null, null, null, null, id.toString(), null));

        Circuitbreaker loaded = get.execute(id.toString());
        assertEquals(CircuitbreakerStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
