package et.gov.ethgov.document.application.ocrjob;

import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobRepository;
import java.util.List;
import java.util.Objects;

public final class ListOcrjobUseCase {
    private final OcrjobRepository repository;

    public ListOcrjobUseCase(OcrjobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Ocrjob> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
