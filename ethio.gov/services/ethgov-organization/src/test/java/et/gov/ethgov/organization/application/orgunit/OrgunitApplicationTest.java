package et.gov.ethgov.organization.application.orgunit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryOrgunitRepository;
import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgunitApplicationTest {
    private InMemoryOrgunitRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrgunitRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOrgunitUseCase(repository, clock);
        var activate = new ActivateOrgunitUseCase(repository, clock);
        var get = new GetOrgunitUseCase(repository);

        var id = create.execute(new CreateOrgunitUseCase.CreateOrgunitCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOrgunitUseCase.ActivateOrgunitCommand(
                null, null, null, null, id.toString(), null));

        Orgunit loaded = get.execute(id.toString());
        assertEquals(OrgunitStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
