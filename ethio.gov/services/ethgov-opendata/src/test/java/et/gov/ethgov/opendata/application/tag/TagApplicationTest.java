package et.gov.ethgov.opendata.application.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryTagRepository;
import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagApplicationTest {
    private InMemoryTagRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTagRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTagUseCase(repository, clock);
        var activate = new ActivateTagUseCase(repository, clock);
        var get = new GetTagUseCase(repository);

        var id = create.execute(new CreateTagUseCase.CreateTagCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTagUseCase.ActivateTagCommand(
                null, null, null, null, id.toString(), null));

        Tag loaded = get.execute(id.toString());
        assertEquals(TagStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
