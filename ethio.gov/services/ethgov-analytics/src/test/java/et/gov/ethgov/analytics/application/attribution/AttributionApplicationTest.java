package et.gov.ethgov.analytics.application.attribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryAttributionRepository;
import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributionApplicationTest {
    private InMemoryAttributionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAttributionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAttributionUseCase(repository, clock);
        var activate = new ActivateAttributionUseCase(repository, clock);
        var get = new GetAttributionUseCase(repository);

        var id = create.execute(new CreateAttributionUseCase.CreateAttributionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAttributionUseCase.ActivateAttributionCommand(
                null, null, null, null, id.toString(), null));

        Attribution loaded = get.execute(id.toString());
        assertEquals(AttributionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
