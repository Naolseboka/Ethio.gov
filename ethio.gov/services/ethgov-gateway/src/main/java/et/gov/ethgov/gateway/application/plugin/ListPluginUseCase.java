package et.gov.ethgov.gateway.application.plugin;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import java.util.List;
import java.util.Objects;

public final class ListPluginUseCase {
    private final PluginRepository repository;

    public ListPluginUseCase(PluginRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Plugin> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
