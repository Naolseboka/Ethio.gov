package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.orgcontact.ActivateOrgcontactUseCase;
import et.gov.ethgov.organization.application.orgcontact.CreateOrgcontactUseCase;
import et.gov.ethgov.organization.application.orgcontact.GetOrgcontactUseCase;
import et.gov.ethgov.organization.application.orgcontact.ListOrgcontactUseCase;
import et.gov.ethgov.organization.application.orgcontact.SuspendOrgcontactUseCase;
import et.gov.ethgov.organization.application.orgcontact.UpdateOrgcontactUseCase;
import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
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
@RequestMapping("/v1/organization/orgcontact")
public class OrgcontactController {
    private final CreateOrgcontactUseCase createUseCase;
    private final UpdateOrgcontactUseCase updateUseCase;
    private final ActivateOrgcontactUseCase activateUseCase;
    private final SuspendOrgcontactUseCase suspendUseCase;
    private final GetOrgcontactUseCase getUseCase;
    private final ListOrgcontactUseCase listUseCase;

    public OrgcontactController(
            CreateOrgcontactUseCase createUseCase,
            UpdateOrgcontactUseCase updateUseCase,
            ActivateOrgcontactUseCase activateUseCase,
            SuspendOrgcontactUseCase suspendUseCase,
            GetOrgcontactUseCase getUseCase,
            ListOrgcontactUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgcontactIdResponse create(@Valid @RequestBody CreateOrgcontactRequest request) {
        var id = createUseCase.execute(new CreateOrgcontactUseCase.CreateOrgcontactCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OrgcontactIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OrgcontactResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OrgcontactResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OrgcontactIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOrgcontactRequest request) {
        var updated = updateUseCase.execute(new UpdateOrgcontactUseCase.UpdateOrgcontactCommand(
                null, request.title(), request.description(), null, id, null));
        return new OrgcontactIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOrgcontactUseCase.ActivateOrgcontactCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOrgcontactRequest request) {
        suspendUseCase.execute(new SuspendOrgcontactUseCase.SuspendOrgcontactCommand(
                null, null, null, null, id, request.reason()));
    }

    private OrgcontactResponse toResponse(Orgcontact aggregate) {
        return new OrgcontactResponse(
                aggregate.id().toString(),
                aggregate.referenceCode(),
                aggregate.title(),
                aggregate.description(),
                aggregate.tenantId(),
                aggregate.status().name(),
                aggregate.createdAt().toString(),
                aggregate.updatedAt().toString(),
                aggregate.version());
    }

    public record CreateOrgcontactRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOrgcontactRequest(String title, String description) {}

    public record SuspendOrgcontactRequest(@NotBlank String reason) {}

    public record OrgcontactIdResponse(String id) {}

    public record OrgcontactResponse(
            String id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            String createdAt,
            String updatedAt,
            long version) {}
}
