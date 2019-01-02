package et.gov.ethgov.analytics.application.forecast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryForecastRepository;
import et.gov.ethgov.analytics.domain.forecast.Forecast;
import et.gov.ethgov.analytics.domain.forecast.ForecastStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForecastApplicationTest {
    private InMemoryForecastRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryForecastRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateForecastUseCase(repository, clock);
        var activate = new ActivateForecastUseCase(repository, clock);
        var get = new GetForecastUseCase(repository);

        var id = create.execute(new CreateForecastUseCase.CreateForecastCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateForecastUseCase.ActivateForecastCommand(
                null, null, null, null, id.toString(), null));

        Forecast loaded = get.execute(id.toString());
        assertEquals(ForecastStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
