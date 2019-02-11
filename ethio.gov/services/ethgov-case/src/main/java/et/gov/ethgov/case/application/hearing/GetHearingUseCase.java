package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.util.Objects;

public final class GetHearingUseCase {
    private final HearingRepository repository;

    public GetHearingUseCase(HearingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Hearing execute(String rawId) {
        HearingId id = HearingId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hearing not found: " + rawId));
    }
}
