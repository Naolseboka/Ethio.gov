package et.gov.ethgov.organization.application.orgunit;

import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitId;
import et.gov.ethgov.organization.domain.orgunit.OrgunitRepository;
import java.util.Objects;

public final class GetOrgunitUseCase {
    private final OrgunitRepository repository;

    public GetOrgunitUseCase(OrgunitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Orgunit execute(String rawId) {
        OrgunitId id = OrgunitId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgunit not found: " + rawId));
    }
}
