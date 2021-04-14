package et.gov.ethgov.gateway.application.rewrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryRewriteRepository;
import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RewriteApplicationTest {
    private InMemoryRewriteRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRewriteRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRewriteUseCase(repository, clock);
        var activate = new ActivateRewriteUseCase(repository, clock);
        var get = new GetRewriteUseCase(repository);

        var id = create.execute(new CreateRewriteUseCase.CreateRewriteCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRewriteUseCase.ActivateRewriteCommand(
                null, null, null, null, id.toString(), null));

        Rewrite loaded = get.execute(id.toString());
        assertEquals(RewriteStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
