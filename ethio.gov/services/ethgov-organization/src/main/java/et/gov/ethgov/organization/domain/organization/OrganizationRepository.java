package et.gov.ethgov.organization.domain.organization;

import java.util.Optional;

public interface OrganizationRepository {

    boolean existsByTin(TinNumber tin);

    Optional<Organization> findById(OrganizationId id);

    Optional<Organization> findByTin(TinNumber tin);

    void save(Organization organization);
}
