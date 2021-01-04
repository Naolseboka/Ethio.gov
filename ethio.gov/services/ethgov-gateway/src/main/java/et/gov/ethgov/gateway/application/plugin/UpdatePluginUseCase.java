package et.gov.ethgov.gateway.application.plugin;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginId;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePluginUseCase {
    private final PluginRepository repository;
    private final Clock clock;

    public UpdatePluginUseCase(PluginRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PluginId execute(UpdatePluginCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PluginId id = PluginId.parse(command.pluginId());
        Plugin aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("plugin not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePluginCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String pluginId,
            String reason) {}
}
