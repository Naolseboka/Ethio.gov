package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.orgunit.ActivateOrgunitUseCase;
import et.gov.ethgov.organization.application.orgunit.CreateOrgunitUseCase;
import et.gov.ethgov.organization.application.orgunit.GetOrgunitUseCase;
import et.gov.ethgov.organization.application.orgunit.ListOrgunitUseCase;
import et.gov.ethgov.organization.application.orgunit.SuspendOrgunitUseCase;
import et.gov.ethgov.organization.application.orgunit.UpdateOrgunitUseCase;
import et.gov.ethgov.organization.domain.orgunit.Orgunit;
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
@RequestMapping("/v1/organization/orgunit")
public class OrgunitController {
    private final CreateOrgunitUseCase createUseCase;
    private final UpdateOrgunitUseCase updateUseCase;
    private final ActivateOrgunitUseCase activateUseCase;
    private final SuspendOrgunitUseCase suspendUseCase;
    private final GetOrgunitUseCase getUseCase;
    private final ListOrgunitUseCase listUseCase;

    public OrgunitController(
            CreateOrgunitUseCase createUseCase,
            UpdateOrgunitUseCase updateUseCase,
            ActivateOrgunitUseCase activateUseCase,
            SuspendOrgunitUseCase suspendUseCase,
            GetOrgunitUseCase getUseCase,
            ListOrgunitUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgunitIdResponse create(@Valid @RequestBody CreateOrgunitRequest request) {
        var id = createUseCase.execute(new CreateOrgunitUseCase.CreateOrgunitCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OrgunitIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OrgunitResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OrgunitResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OrgunitIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOrgunitRequest request) {
        var updated = updateUseCase.execute(new UpdateOrgunitUseCase.UpdateOrgunitCommand(
                null, request.title(), request.description(), null, id, null));
        return new OrgunitIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOrgunitUseCase.ActivateOrgunitCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOrgunitRequest request) {
        suspendUseCase.execute(new SuspendOrgunitUseCase.SuspendOrgunitCommand(
                null, null, null, null, id, request.reason()));
    }

    private OrgunitResponse toResponse(Orgunit aggregate) {
        return new OrgunitResponse(
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

    public record CreateOrgunitRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOrgunitRequest(String title, String description) {}

    public record SuspendOrgunitRequest(@NotBlank String reason) {}

    public record OrgunitIdResponse(String id) {}

    public record OrgunitResponse(
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
