package et.gov.ethgov.case.application.appeal;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealId;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import java.util.Objects;

public final class GetAppealUseCase {
    private final AppealRepository repository;

    public GetAppealUseCase(AppealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Appeal execute(String rawId) {
        AppealId id = AppealId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("appeal not found: " + rawId));
    }
}
