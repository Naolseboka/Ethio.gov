package et.gov.ethgov.citizen.application.educationrecord;

import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordId;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordRepository;
import java.util.Objects;

public final class GetEducationrecordUseCase {
    private final EducationrecordRepository repository;

    public GetEducationrecordUseCase(EducationrecordRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Educationrecord execute(String rawId) {
        EducationrecordId id = EducationrecordId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("educationrecord not found: " + rawId));
    }
}
