package et.gov.ethgov.directory.domain.staffroster;

import java.util.List;
import java.util.Optional;

public interface StaffrosterRepository {
    Staffroster save(Staffroster aggregate);

    Optional<Staffroster> findById(StaffrosterId id);

    Optional<Staffroster> findByReferenceCode(String tenantId, String referenceCode);

    List<Staffroster> findByTenant(String tenantId, int limit);

    void delete(StaffrosterId id);
}
