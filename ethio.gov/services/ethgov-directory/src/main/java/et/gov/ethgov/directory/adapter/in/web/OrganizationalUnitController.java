package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.unit.CreateOrganizationalUnitUseCase;
import et.gov.ethgov.directory.application.unit.DeactivateOrganizationalUnitUseCase;
import et.gov.ethgov.directory.application.unit.GetOrganizationalUnitUseCase;
import et.gov.ethgov.directory.application.unit.MoveOrganizationalUnitUseCase;
import et.gov.ethgov.directory.application.unit.ReactivateOrganizationalUnitUseCase;
import et.gov.ethgov.directory.application.unit.UpdateOrganizationalUnitUseCase;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organizational-units")
public class OrganizationalUnitController {

    private final CreateOrganizationalUnitUseCase createUnit;
    private final UpdateOrganizationalUnitUseCase updateUnit;
    private final MoveOrganizationalUnitUseCase moveUnit;
    private final DeactivateOrganizationalUnitUseCase deactivateUnit;
    private final ReactivateOrganizationalUnitUseCase reactivateUnit;
    private final GetOrganizationalUnitUseCase getUnit;

    public OrganizationalUnitController(
            CreateOrganizationalUnitUseCase createUnit,
            UpdateOrganizationalUnitUseCase updateUnit,
            MoveOrganizationalUnitUseCase moveUnit,
            DeactivateOrganizationalUnitUseCase deactivateUnit,
            ReactivateOrganizationalUnitUseCase reactivateUnit,
            GetOrganizationalUnitUseCase getUnit) {
        this.createUnit = createUnit;
        this.updateUnit = updateUnit;
        this.moveUnit = moveUnit;
        this.deactivateUnit = deactivateUnit;
        this.reactivateUnit = reactivateUnit;
        this.getUnit = getUnit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitIdResponse create(@Valid @RequestBody CreateUnitRequest request) {
        OrganizationalUnitId id = createUnit.execute(new CreateOrganizationalUnitUseCase.Command(
                request.tenantId(),
                request.code(),
                request.name(),
                request.type(),
                request.parentId(),
                request.linkedOrganizationId(),
                request.description()));
        return new UnitIdResponse(id.value());
    }

    @GetMapping("/lookup")
    public UnitResponse lookup(@RequestParam String tenantId, @RequestParam String code) {
        return UnitResponse.from(getUnit.byTenantAndCode(tenantId, code));
    }

    @GetMapping("/{organizationalUnitId}")
    public UnitResponse get(@PathVariable String organizationalUnitId) {
        return UnitResponse.from(getUnit.byId(organizationalUnitId));
    }

    @GetMapping("/{organizationalUnitId}/children")
    public List<UnitResponse> children(@PathVariable String organizationalUnitId) {
        return getUnit.children(organizationalUnitId).stream().map(UnitResponse::from).toList();
    }

    @PutMapping("/{organizationalUnitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable String organizationalUnitId, @Valid @RequestBody UpdateUnitRequest request) {
        updateUnit.execute(new UpdateOrganizationalUnitUseCase.Command(
                organizationalUnitId, request.name(), request.linkedOrganizationId(), request.description()));
    }

    @PostMapping("/{organizationalUnitId}/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void move(@PathVariable String organizationalUnitId, @Valid @RequestBody MoveUnitRequest request) {
        moveUnit.execute(organizationalUnitId, request.newParentId());
    }

    @PostMapping("/{organizationalUnitId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String organizationalUnitId) {
        deactivateUnit.execute(organizationalUnitId);
    }

    @PostMapping("/{organizationalUnitId}/reactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivate(@PathVariable String organizationalUnitId) {
        reactivateUnit.execute(organizationalUnitId);
    }

    public record CreateUnitRequest(
            @NotBlank String tenantId,
            @NotBlank String code,
            @NotBlank String name,
            @NotBlank String type,
            String parentId,
            String linkedOrganizationId,
            String description) {}

    public record UpdateUnitRequest(
            @NotBlank String name, String linkedOrganizationId, String description) {}

    public record MoveUnitRequest(String newParentId) {}

    public record UnitIdResponse(String organizationalUnitId) {}

    public record UnitResponse(
            String organizationalUnitId,
            String tenantId,
            String code,
            String name,
            String type,
            String status,
            String parentId,
            String linkedOrganizationId,
            String description) {

        static UnitResponse from(OrganizationalUnit unit) {
            return new UnitResponse(
                    unit.id().value(),
                    unit.tenantId().value(),
                    unit.code().value(),
                    unit.name().value(),
                    unit.type().name(),
                    unit.status().name(),
                    unit.parentId().map(OrganizationalUnitId::value).orElse(null),
                    unit.linkedOrganizationId().orElse(null),
                    unit.description().orElse(null));
        }
    }
}
