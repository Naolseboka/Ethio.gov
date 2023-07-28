package et.gov.ethgov.organization.application.orgaddress;

import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressId;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressRepository;
import java.util.Objects;

public final class GetOrgaddressUseCase {
    private final OrgaddressRepository repository;

    public GetOrgaddressUseCase(OrgaddressRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Orgaddress execute(String rawId) {
        OrgaddressId id = OrgaddressId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgaddress not found: " + rawId));
    }
}
