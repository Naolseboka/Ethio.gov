package et.gov.ethgov.gateway.application.plugin;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginId;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import java.util.Objects;

public final class GetPluginUseCase {
    private final PluginRepository repository;

    public GetPluginUseCase(PluginRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Plugin execute(String rawId) {
        PluginId id = PluginId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("plugin not found: " + rawId));
    }
}
