package et.gov.ethgov.organization.application.mandate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryMandateRepository;
import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MandateApplicationTest {
    private InMemoryMandateRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMandateRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMandateUseCase(repository, clock);
        var activate = new ActivateMandateUseCase(repository, clock);
        var get = new GetMandateUseCase(repository);

        var id = create.execute(new CreateMandateUseCase.CreateMandateCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMandateUseCase.ActivateMandateCommand(
                null, null, null, null, id.toString(), null));

        Mandate loaded = get.execute(id.toString());
        assertEquals(MandateStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
