package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.orgaddress.ActivateOrgaddressUseCase;
import et.gov.ethgov.organization.application.orgaddress.CreateOrgaddressUseCase;
import et.gov.ethgov.organization.application.orgaddress.GetOrgaddressUseCase;
import et.gov.ethgov.organization.application.orgaddress.ListOrgaddressUseCase;
import et.gov.ethgov.organization.application.orgaddress.SuspendOrgaddressUseCase;
import et.gov.ethgov.organization.application.orgaddress.UpdateOrgaddressUseCase;
import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
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
@RequestMapping("/v1/organization/orgaddress")
public class OrgaddressController {
    private final CreateOrgaddressUseCase createUseCase;
    private final UpdateOrgaddressUseCase updateUseCase;
    private final ActivateOrgaddressUseCase activateUseCase;
    private final SuspendOrgaddressUseCase suspendUseCase;
    private final GetOrgaddressUseCase getUseCase;
    private final ListOrgaddressUseCase listUseCase;

    public OrgaddressController(
            CreateOrgaddressUseCase createUseCase,
            UpdateOrgaddressUseCase updateUseCase,
            ActivateOrgaddressUseCase activateUseCase,
            SuspendOrgaddressUseCase suspendUseCase,
            GetOrgaddressUseCase getUseCase,
            ListOrgaddressUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgaddressIdResponse create(@Valid @RequestBody CreateOrgaddressRequest request) {
        var id = createUseCase.execute(new CreateOrgaddressUseCase.CreateOrgaddressCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OrgaddressIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OrgaddressResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OrgaddressResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OrgaddressIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOrgaddressRequest request) {
        var updated = updateUseCase.execute(new UpdateOrgaddressUseCase.UpdateOrgaddressCommand(
                null, request.title(), request.description(), null, id, null));
        return new OrgaddressIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOrgaddressUseCase.ActivateOrgaddressCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOrgaddressRequest request) {
        suspendUseCase.execute(new SuspendOrgaddressUseCase.SuspendOrgaddressCommand(
                null, null, null, null, id, request.reason()));
    }

    private OrgaddressResponse toResponse(Orgaddress aggregate) {
        return new OrgaddressResponse(
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

    public record CreateOrgaddressRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOrgaddressRequest(String title, String description) {}

    public record SuspendOrgaddressRequest(@NotBlank String reason) {}

    public record OrgaddressIdResponse(String id) {}

    public record OrgaddressResponse(
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
