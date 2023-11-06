package et.gov.ethgov.organization.application.servicecatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryServicecatalogRepository;
import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicecatalogApplicationTest {
    private InMemoryServicecatalogRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryServicecatalogRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateServicecatalogUseCase(repository, clock);
        var activate = new ActivateServicecatalogUseCase(repository, clock);
        var get = new GetServicecatalogUseCase(repository);

        var id = create.execute(new CreateServicecatalogUseCase.CreateServicecatalogCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateServicecatalogUseCase.ActivateServicecatalogCommand(
                null, null, null, null, id.toString(), null));

        Servicecatalog loaded = get.execute(id.toString());
        assertEquals(ServicecatalogStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
