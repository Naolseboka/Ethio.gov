package et.gov.ethgov.document.domain.ocrjob;

import java.util.List;
import java.util.Optional;

public interface OcrjobRepository {
    Ocrjob save(Ocrjob aggregate);

    Optional<Ocrjob> findById(OcrjobId id);

    Optional<Ocrjob> findByReferenceCode(String tenantId, String referenceCode);

    List<Ocrjob> findByTenant(String tenantId, int limit);

    void delete(OcrjobId id);
}
