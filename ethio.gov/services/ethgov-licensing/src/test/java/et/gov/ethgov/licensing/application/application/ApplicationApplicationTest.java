package et.gov.ethgov.licensing.application.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryApplicationRepository;
import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationApplicationTest {
    private InMemoryApplicationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryApplicationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateApplicationUseCase(repository, clock);
        var activate = new ActivateApplicationUseCase(repository, clock);
        var get = new GetApplicationUseCase(repository);

        var id = create.execute(new CreateApplicationUseCase.CreateApplicationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateApplicationUseCase.ActivateApplicationCommand(
                null, null, null, null, id.toString(), null));

        Application loaded = get.execute(id.toString());
        assertEquals(ApplicationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
