package et.gov.ethgov.licensing.application.renewal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryRenewalRepository;
import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RenewalApplicationTest {
    private InMemoryRenewalRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRenewalRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRenewalUseCase(repository, clock);
        var activate = new ActivateRenewalUseCase(repository, clock);
        var get = new GetRenewalUseCase(repository);

        var id = create.execute(new CreateRenewalUseCase.CreateRenewalCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRenewalUseCase.ActivateRenewalCommand(
                null, null, null, null, id.toString(), null));

        Renewal loaded = get.execute(id.toString());
        assertEquals(RenewalStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
