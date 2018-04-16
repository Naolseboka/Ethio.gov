package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.organization.AssignTinUseCase;
import et.gov.ethgov.organization.application.organization.DissolveOrganizationUseCase;
import et.gov.ethgov.organization.application.organization.GetOrganizationUseCase;
import et.gov.ethgov.organization.application.organization.RegisterOrganizationUseCase;
import et.gov.ethgov.organization.application.organization.ReinstateOrganizationUseCase;
import et.gov.ethgov.organization.application.organization.SuspendOrganizationUseCase;
import et.gov.ethgov.organization.application.organization.UpdateOrganizationProfileUseCase;
import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.platform.commons.error.DomainException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/organizations")
public class OrganizationController {

    private final RegisterOrganizationUseCase registerOrganization;
    private final UpdateOrganizationProfileUseCase updateProfile;
    private final AssignTinUseCase assignTin;
    private final SuspendOrganizationUseCase suspendOrganization;
    private final ReinstateOrganizationUseCase reinstateOrganization;
    private final DissolveOrganizationUseCase dissolveOrganization;
    private final GetOrganizationUseCase getOrganization;

    public OrganizationController(
            RegisterOrganizationUseCase registerOrganization,
            UpdateOrganizationProfileUseCase updateProfile,
            AssignTinUseCase assignTin,
            SuspendOrganizationUseCase suspendOrganization,
            ReinstateOrganizationUseCase reinstateOrganization,
            DissolveOrganizationUseCase dissolveOrganization,
            GetOrganizationUseCase getOrganization) {
        this.registerOrganization = registerOrganization;
        this.updateProfile = updateProfile;
        this.assignTin = assignTin;
        this.suspendOrganization = suspendOrganization;
        this.reinstateOrganization = reinstateOrganization;
        this.dissolveOrganization = dissolveOrganization;
        this.getOrganization = getOrganization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationIdResponse register(@Valid @RequestBody RegisterOrganizationRequest request) {
        OrganizationId id = registerOrganization.execute(toRegisterCommand(request));
        return new OrganizationIdResponse(id.value());
    }

    @GetMapping("/search")
    public OrganizationResponse searchByTin(@RequestParam String tin) {
        return getOrganization
                .byTin(tin)
                .map(OrganizationResponse::from)
                .orElseThrow(() -> new DomainException(
                        "ORGANIZATION_NOT_FOUND",
                        "Organization Not Found",
                        "Organization does not exist",
                        404));
    }

    @GetMapping("/{organizationId}")
    public OrganizationResponse get(@PathVariable String organizationId) {
        return OrganizationResponse.from(getOrganization.byId(organizationId));
    }

    @PutMapping("/{organizationId}/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @PathVariable String organizationId, @Valid @RequestBody UpdateProfileRequest request) {
        updateProfile.execute(toUpdateCommand(organizationId, request));
    }

    @PostMapping("/{organizationId}/tin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignTin(@PathVariable String organizationId, @Valid @RequestBody AssignTinRequest request) {
        assignTin.execute(organizationId, request.tin());
    }

    @PostMapping("/{organizationId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable String organizationId, @Valid @RequestBody SuspendRequest request) {
        suspendOrganization.execute(organizationId, request.reason());
    }

    @PostMapping("/{organizationId}/reinstate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reinstate(@PathVariable String organizationId) {
        reinstateOrganization.execute(organizationId);
    }

    @PostMapping("/{organizationId}/dissolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dissolve(@PathVariable String organizationId) {
        dissolveOrganization.execute(organizationId);
    }

    private static RegisterOrganizationUseCase.Command toRegisterCommand(RegisterOrganizationRequest request) {
        return new RegisterOrganizationUseCase.Command(
                request.legalName(),
                request.tradeName(),
                request.type(),
                request.phone(),
                request.email(),
                request.registeringTenantId(),
                request.ownerCitizenId(),
                request.establishedOn(),
                request.regionCode(),
                request.zoneCode(),
                request.zoneName(),
                request.woredaCode(),
                request.woredaName(),
                request.kebeleCode(),
                request.kebeleName(),
                request.locality(),
                request.street(),
                request.houseNumber());
    }

    private static UpdateOrganizationProfileUseCase.Command toUpdateCommand(
            String organizationId, UpdateProfileRequest request) {
        return new UpdateOrganizationProfileUseCase.Command(
                organizationId,
                request.legalName(),
                request.tradeName(),
                request.phone(),
                request.email(),
                request.registrationNumber(),
                request.establishedOn(),
                request.regionCode(),
                request.zoneCode(),
                request.zoneName(),
                request.woredaCode(),
                request.woredaName(),
                request.kebeleCode(),
                request.kebeleName(),
                request.locality(),
                request.street(),
                request.houseNumber());
    }

    public record RegisterOrganizationRequest(
            @NotBlank String legalName,
            String tradeName,
            @NotBlank String type,
            @NotBlank String phone,
            String email,
            @NotBlank String registeringTenantId,
            String ownerCitizenId,
            String establishedOn,
            @NotBlank String regionCode,
            @NotBlank String zoneCode,
            @NotBlank String zoneName,
            @NotBlank String woredaCode,
            @NotBlank String woredaName,
            @NotBlank String kebeleCode,
            @NotBlank String kebeleName,
            String locality,
            String street,
            String houseNumber) {}

    public record UpdateProfileRequest(
            @NotBlank String legalName,
            String tradeName,
            @NotBlank String phone,
            String email,
            String registrationNumber,
            String establishedOn,
            @NotBlank String regionCode,
            @NotBlank String zoneCode,
            @NotBlank String zoneName,
            @NotBlank String woredaCode,
            @NotBlank String woredaName,
            @NotBlank String kebeleCode,
            @NotBlank String kebeleName,
            String locality,
            String street,
            String houseNumber) {}

    public record AssignTinRequest(@NotBlank @Size(min = 10, max = 10) String tin) {}

    public record SuspendRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record OrganizationIdResponse(String organizationId) {}

    public record OrganizationResponse(
            String organizationId,
            String legalName,
            String tradeName,
            String type,
            String status,
            String tin,
            String registrationNumber,
            String phone,
            String email,
            String registeringTenantId,
            String ownerCitizenId,
            String establishedOn,
            String suspensionReason,
            String regionCode,
            String zoneCode,
            String woredaCode,
            String kebeleCode) {

        static OrganizationResponse from(Organization organization) {
            return new OrganizationResponse(
                    organization.id().value(),
                    organization.legalName().value(),
                    organization.tradeName().orElse(null),
                    organization.type().name(),
                    organization.status().name(),
                    organization.tin().map(Object::toString).orElse(null),
                    organization.registrationNumber().orElse(null),
                    organization.phone().e164(),
                    organization.email().orElse(null),
                    organization.registeringTenantId().value(),
                    organization.ownerCitizenId().orElse(null),
                    organization.establishedOn().map(Object::toString).orElse(null),
                    organization.suspensionReason().orElse(null),
                    organization.registeredAddress().regionalState().code(),
                    organization.registeredAddress().zoneCode(),
                    organization.registeredAddress().woredaCode(),
                    organization.registeredAddress().kebeleCode());
        }
    }
}
