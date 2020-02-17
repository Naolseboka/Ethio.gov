package et.gov.ethgov.developer.application.quota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryQuotaRepository;
import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuotaApplicationTest {
    private InMemoryQuotaRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQuotaRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateQuotaUseCase(repository, clock);
        var activate = new ActivateQuotaUseCase(repository, clock);
        var get = new GetQuotaUseCase(repository);

        var id = create.execute(new CreateQuotaUseCase.CreateQuotaCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateQuotaUseCase.ActivateQuotaCommand(
                null, null, null, null, id.toString(), null));

        Quota loaded = get.execute(id.toString());
        assertEquals(QuotaStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
