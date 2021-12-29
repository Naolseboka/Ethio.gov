package et.gov.ethgov.identity.application.apikey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryApikeyRepository;
import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApikeyApplicationTest {
    private InMemoryApikeyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryApikeyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateApikeyUseCase(repository, clock);
        var activate = new ActivateApikeyUseCase(repository, clock);
        var get = new GetApikeyUseCase(repository);

        var id = create.execute(new CreateApikeyUseCase.CreateApikeyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateApikeyUseCase.ActivateApikeyCommand(
                null, null, null, null, id.toString(), null));

        Apikey loaded = get.execute(id.toString());
        assertEquals(ApikeyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
