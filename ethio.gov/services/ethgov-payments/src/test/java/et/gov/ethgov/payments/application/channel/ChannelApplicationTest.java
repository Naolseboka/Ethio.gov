package et.gov.ethgov.payments.application.channel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryChannelRepository;
import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChannelApplicationTest {
    private InMemoryChannelRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChannelRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateChannelUseCase(repository, clock);
        var activate = new ActivateChannelUseCase(repository, clock);
        var get = new GetChannelUseCase(repository);

        var id = create.execute(new CreateChannelUseCase.CreateChannelCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateChannelUseCase.ActivateChannelCommand(
                null, null, null, null, id.toString(), null));

        Channel loaded = get.execute(id.toString());
        assertEquals(ChannelStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
