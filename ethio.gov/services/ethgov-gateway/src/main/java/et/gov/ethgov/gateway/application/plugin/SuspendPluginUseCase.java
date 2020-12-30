package et.gov.ethgov.gateway.application.plugin;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginId;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPluginUseCase {
    private final PluginRepository repository;
    private final Clock clock;

    public SuspendPluginUseCase(PluginRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PluginId execute(SuspendPluginCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PluginId id = PluginId.parse(command.pluginId());
        Plugin aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("plugin not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPluginCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String pluginId,
            String reason) {}
}
