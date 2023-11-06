package et.gov.ethgov.organization.application.membership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryMembershipRepository;
import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MembershipApplicationTest {
    private InMemoryMembershipRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMembershipRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMembershipUseCase(repository, clock);
        var activate = new ActivateMembershipUseCase(repository, clock);
        var get = new GetMembershipUseCase(repository);

        var id = create.execute(new CreateMembershipUseCase.CreateMembershipCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMembershipUseCase.ActivateMembershipCommand(
                null, null, null, null, id.toString(), null));

        Membership loaded = get.execute(id.toString());
        assertEquals(MembershipStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
