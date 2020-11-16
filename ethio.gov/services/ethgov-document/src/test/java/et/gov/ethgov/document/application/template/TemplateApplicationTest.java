package et.gov.ethgov.document.application.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryTemplateRepository;
import et.gov.ethgov.document.domain.template.Template;
import et.gov.ethgov.document.domain.template.TemplateStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemplateApplicationTest {
    private InMemoryTemplateRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTemplateRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTemplateUseCase(repository, clock);
        var activate = new ActivateTemplateUseCase(repository, clock);
        var get = new GetTemplateUseCase(repository);

        var id = create.execute(new CreateTemplateUseCase.CreateTemplateCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTemplateUseCase.ActivateTemplateCommand(
                null, null, null, null, id.toString(), null));

        Template loaded = get.execute(id.toString());
        assertEquals(TemplateStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
