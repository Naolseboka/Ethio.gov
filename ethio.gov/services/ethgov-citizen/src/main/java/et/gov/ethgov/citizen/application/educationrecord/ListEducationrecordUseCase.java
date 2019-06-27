package et.gov.ethgov.citizen.application.educationrecord;

import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordRepository;
import java.util.List;
import java.util.Objects;

public final class ListEducationrecordUseCase {
    private final EducationrecordRepository repository;

    public ListEducationrecordUseCase(EducationrecordRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Educationrecord> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
