package et.gov.ethgov.document.application.ocrjob;

import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobId;
import et.gov.ethgov.document.domain.ocrjob.OcrjobRepository;
import java.util.Objects;

public final class GetOcrjobUseCase {
    private final OcrjobRepository repository;

    public GetOcrjobUseCase(OcrjobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Ocrjob execute(String rawId) {
        OcrjobId id = OcrjobId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ocrjob not found: " + rawId));
    }
}
