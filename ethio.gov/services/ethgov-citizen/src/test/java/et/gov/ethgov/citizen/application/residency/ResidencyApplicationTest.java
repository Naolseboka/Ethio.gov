package et.gov.ethgov.citizen.application.residency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryResidencyRepository;
import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResidencyApplicationTest {
    private InMemoryResidencyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryResidencyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateResidencyUseCase(repository, clock);
        var activate = new ActivateResidencyUseCase(repository, clock);
        var get = new GetResidencyUseCase(repository);

        var id = create.execute(new CreateResidencyUseCase.CreateResidencyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateResidencyUseCase.ActivateResidencyCommand(
                null, null, null, null, id.toString(), null));

        Residency loaded = get.execute(id.toString());
        assertEquals(ResidencyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
