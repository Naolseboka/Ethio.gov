package et.gov.ethgov.citizen.domain.educationrecord;

import java.util.List;
import java.util.Optional;

public interface EducationrecordRepository {
    Educationrecord save(Educationrecord aggregate);

    Optional<Educationrecord> findById(EducationrecordId id);

    Optional<Educationrecord> findByReferenceCode(String tenantId, String referenceCode);

    List<Educationrecord> findByTenant(String tenantId, int limit);

    void delete(EducationrecordId id);
}
