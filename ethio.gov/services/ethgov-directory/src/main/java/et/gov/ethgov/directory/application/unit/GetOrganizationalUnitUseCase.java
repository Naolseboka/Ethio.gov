package et.gov.ethgov.directory.application.unit;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.UnitCode;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository units;

    public GetOrganizationalUnitUseCase(OrganizationalUnitRepository units) {
        this.units = units;
    }

    @Transactional(readOnly = true)
    public OrganizationalUnit byId(String organizationalUnitId) {
        return units.findById(OrganizationalUnitId.of(organizationalUnitId))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public OrganizationalUnit byTenantAndCode(String tenantId, String code) {
        return units.findByTenantAndCode(TenantId.of(tenantId), UnitCode.of(code))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public List<OrganizationalUnit> children(String parentId) {
        return units.findChildren(OrganizationalUnitId.of(parentId));
    }
}
