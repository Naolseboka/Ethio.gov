package et.gov.ethgov.gateway.domain.plugin;

import java.util.List;
import java.util.Optional;

public interface PluginRepository {
    Plugin save(Plugin aggregate);

    Optional<Plugin> findById(PluginId id);

    Optional<Plugin> findByReferenceCode(String tenantId, String referenceCode);

    List<Plugin> findByTenant(String tenantId, int limit);

    void delete(PluginId id);
}
