package et.gov.ethgov.gateway.application.plugin;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginId;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePluginUseCase {
    private final PluginRepository repository;
    private final Clock clock;

    public CreatePluginUseCase(PluginRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PluginId execute(CreatePluginCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Plugin aggregate = Plugin.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePluginCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String pluginId,
            String reason) {}
}
