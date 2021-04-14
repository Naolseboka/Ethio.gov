package et.gov.ethgov.gateway.application.upstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryUpstreamRepository;
import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpstreamApplicationTest {
    private InMemoryUpstreamRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUpstreamRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateUpstreamUseCase(repository, clock);
        var activate = new ActivateUpstreamUseCase(repository, clock);
        var get = new GetUpstreamUseCase(repository);

        var id = create.execute(new CreateUpstreamUseCase.CreateUpstreamCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateUpstreamUseCase.ActivateUpstreamCommand(
                null, null, null, null, id.toString(), null));

        Upstream loaded = get.execute(id.toString());
        assertEquals(UpstreamStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
