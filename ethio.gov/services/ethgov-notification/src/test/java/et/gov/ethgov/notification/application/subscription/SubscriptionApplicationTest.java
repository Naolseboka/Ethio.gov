package et.gov.ethgov.notification.application.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemorySubscriptionRepository;
import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscriptionApplicationTest {
    private InMemorySubscriptionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySubscriptionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSubscriptionUseCase(repository, clock);
        var activate = new ActivateSubscriptionUseCase(repository, clock);
        var get = new GetSubscriptionUseCase(repository);

        var id = create.execute(new CreateSubscriptionUseCase.CreateSubscriptionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSubscriptionUseCase.ActivateSubscriptionCommand(
                null, null, null, null, id.toString(), null));

        Subscription loaded = get.execute(id.toString());
        assertEquals(SubscriptionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
