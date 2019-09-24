package et.gov.ethgov.citizen.application.disabilitystatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryDisabilitystatusRepository;
import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisabilitystatusApplicationTest {
    private InMemoryDisabilitystatusRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDisabilitystatusRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDisabilitystatusUseCase(repository, clock);
        var activate = new ActivateDisabilitystatusUseCase(repository, clock);
        var get = new GetDisabilitystatusUseCase(repository);

        var id = create.execute(new CreateDisabilitystatusUseCase.CreateDisabilitystatusCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDisabilitystatusUseCase.ActivateDisabilitystatusCommand(
                null, null, null, null, id.toString(), null));

        Disabilitystatus loaded = get.execute(id.toString());
        assertEquals(DisabilitystatusStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
