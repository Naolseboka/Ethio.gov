package et.gov.ethgov.licensing.application.license;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryLicenseRepository;
import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LicenseApplicationTest {
    private InMemoryLicenseRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLicenseRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLicenseUseCase(repository, clock);
        var activate = new ActivateLicenseUseCase(repository, clock);
        var get = new GetLicenseUseCase(repository);

        var id = create.execute(new CreateLicenseUseCase.CreateLicenseCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLicenseUseCase.ActivateLicenseCommand(
                null, null, null, null, id.toString(), null));

        License loaded = get.execute(id.toString());
        assertEquals(LicenseStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
