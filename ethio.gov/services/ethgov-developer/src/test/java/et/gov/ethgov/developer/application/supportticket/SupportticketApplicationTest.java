package et.gov.ethgov.developer.application.supportticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemorySupportticketRepository;
import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupportticketApplicationTest {
    private InMemorySupportticketRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySupportticketRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSupportticketUseCase(repository, clock);
        var activate = new ActivateSupportticketUseCase(repository, clock);
        var get = new GetSupportticketUseCase(repository);

        var id = create.execute(new CreateSupportticketUseCase.CreateSupportticketCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSupportticketUseCase.ActivateSupportticketCommand(
                null, null, null, null, id.toString(), null));

        Supportticket loaded = get.execute(id.toString());
        assertEquals(SupportticketStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
