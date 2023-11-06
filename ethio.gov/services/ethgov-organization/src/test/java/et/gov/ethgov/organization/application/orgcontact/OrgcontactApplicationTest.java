package et.gov.ethgov.organization.application.orgcontact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryOrgcontactRepository;
import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgcontactApplicationTest {
    private InMemoryOrgcontactRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrgcontactRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOrgcontactUseCase(repository, clock);
        var activate = new ActivateOrgcontactUseCase(repository, clock);
        var get = new GetOrgcontactUseCase(repository);

        var id = create.execute(new CreateOrgcontactUseCase.CreateOrgcontactCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOrgcontactUseCase.ActivateOrgcontactCommand(
                null, null, null, null, id.toString(), null));

        Orgcontact loaded = get.execute(id.toString());
        assertEquals(OrgcontactStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
