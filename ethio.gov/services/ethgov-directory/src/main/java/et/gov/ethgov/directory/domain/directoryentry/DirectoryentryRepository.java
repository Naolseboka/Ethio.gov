package et.gov.ethgov.directory.domain.directoryentry;

import java.util.List;
import java.util.Optional;

public interface DirectoryentryRepository {
    Directoryentry save(Directoryentry aggregate);

    Optional<Directoryentry> findById(DirectoryentryId id);

    Optional<Directoryentry> findByReferenceCode(String tenantId, String referenceCode);

    List<Directoryentry> findByTenant(String tenantId, int limit);

    void delete(DirectoryentryId id);
}
