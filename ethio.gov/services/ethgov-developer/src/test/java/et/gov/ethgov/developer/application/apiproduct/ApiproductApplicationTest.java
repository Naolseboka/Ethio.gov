package et.gov.ethgov.developer.application.apiproduct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryApiproductRepository;
import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiproductApplicationTest {
    private InMemoryApiproductRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryApiproductRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateApiproductUseCase(repository, clock);
        var activate = new ActivateApiproductUseCase(repository, clock);
        var get = new GetApiproductUseCase(repository);

        var id = create.execute(new CreateApiproductUseCase.CreateApiproductCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateApiproductUseCase.ActivateApiproductCommand(
                null, null, null, null, id.toString(), null));

        Apiproduct loaded = get.execute(id.toString());
        assertEquals(ApiproductStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
