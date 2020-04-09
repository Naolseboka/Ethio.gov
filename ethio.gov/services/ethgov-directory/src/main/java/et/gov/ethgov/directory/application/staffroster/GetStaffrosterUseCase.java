package et.gov.ethgov.directory.application.staffroster;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterId;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import java.util.Objects;

public final class GetStaffrosterUseCase {
    private final StaffrosterRepository repository;

    public GetStaffrosterUseCase(StaffrosterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Staffroster execute(String rawId) {
        StaffrosterId id = StaffrosterId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("staffroster not found: " + rawId));
    }
}
