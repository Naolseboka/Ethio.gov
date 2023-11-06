package et.gov.ethgov.organization.application.orgaddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryOrgaddressRepository;
import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgaddressApplicationTest {
    private InMemoryOrgaddressRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrgaddressRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOrgaddressUseCase(repository, clock);
        var activate = new ActivateOrgaddressUseCase(repository, clock);
        var get = new GetOrgaddressUseCase(repository);

        var id = create.execute(new CreateOrgaddressUseCase.CreateOrgaddressCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOrgaddressUseCase.ActivateOrgaddressCommand(
                null, null, null, null, id.toString(), null));

        Orgaddress loaded = get.execute(id.toString());
        assertEquals(OrgaddressStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
