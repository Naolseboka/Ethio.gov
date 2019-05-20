package et.gov.ethgov.case.application.closure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryClosureRepository;
import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClosureApplicationTest {
    private InMemoryClosureRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryClosureRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateClosureUseCase(repository, clock);
        var activate = new ActivateClosureUseCase(repository, clock);
        var get = new GetClosureUseCase(repository);

        var id = create.execute(new CreateClosureUseCase.CreateClosureCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateClosureUseCase.ActivateClosureCommand(
                null, null, null, null, id.toString(), null));

        Closure loaded = get.execute(id.toString());
        assertEquals(ClosureStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
