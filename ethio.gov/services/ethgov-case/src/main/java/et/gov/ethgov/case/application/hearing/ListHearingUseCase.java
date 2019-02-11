package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.util.List;
import java.util.Objects;

public final class ListHearingUseCase {
    private final HearingRepository repository;

    public ListHearingUseCase(HearingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Hearing> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
