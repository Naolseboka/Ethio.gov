package et.gov.ethgov.licensing.application.licensefee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryLicensefeeRepository;
import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LicensefeeApplicationTest {
    private InMemoryLicensefeeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLicensefeeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLicensefeeUseCase(repository, clock);
        var activate = new ActivateLicensefeeUseCase(repository, clock);
        var get = new GetLicensefeeUseCase(repository);

        var id = create.execute(new CreateLicensefeeUseCase.CreateLicensefeeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLicensefeeUseCase.ActivateLicensefeeCommand(
                null, null, null, null, id.toString(), null));

        Licensefee loaded = get.execute(id.toString());
        assertEquals(LicensefeeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
