package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.partnerlink.ActivatePartnerlinkUseCase;
import et.gov.ethgov.organization.application.partnerlink.CreatePartnerlinkUseCase;
import et.gov.ethgov.organization.application.partnerlink.GetPartnerlinkUseCase;
import et.gov.ethgov.organization.application.partnerlink.ListPartnerlinkUseCase;
import et.gov.ethgov.organization.application.partnerlink.SuspendPartnerlinkUseCase;
import et.gov.ethgov.organization.application.partnerlink.UpdatePartnerlinkUseCase;
import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
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
@RequestMapping("/v1/organization/partnerlink")
public class PartnerlinkController {
    private final CreatePartnerlinkUseCase createUseCase;
    private final UpdatePartnerlinkUseCase updateUseCase;
    private final ActivatePartnerlinkUseCase activateUseCase;
    private final SuspendPartnerlinkUseCase suspendUseCase;
    private final GetPartnerlinkUseCase getUseCase;
    private final ListPartnerlinkUseCase listUseCase;

    public PartnerlinkController(
            CreatePartnerlinkUseCase createUseCase,
            UpdatePartnerlinkUseCase updateUseCase,
            ActivatePartnerlinkUseCase activateUseCase,
            SuspendPartnerlinkUseCase suspendUseCase,
            GetPartnerlinkUseCase getUseCase,
            ListPartnerlinkUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartnerlinkIdResponse create(@Valid @RequestBody CreatePartnerlinkRequest request) {
        var id = createUseCase.execute(new CreatePartnerlinkUseCase.CreatePartnerlinkCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PartnerlinkIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PartnerlinkResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PartnerlinkResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PartnerlinkIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePartnerlinkRequest request) {
        var updated = updateUseCase.execute(new UpdatePartnerlinkUseCase.UpdatePartnerlinkCommand(
                null, request.title(), request.description(), null, id, null));
        return new PartnerlinkIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePartnerlinkUseCase.ActivatePartnerlinkCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPartnerlinkRequest request) {
        suspendUseCase.execute(new SuspendPartnerlinkUseCase.SuspendPartnerlinkCommand(
                null, null, null, null, id, request.reason()));
    }

    private PartnerlinkResponse toResponse(Partnerlink aggregate) {
        return new PartnerlinkResponse(
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

    public record CreatePartnerlinkRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePartnerlinkRequest(String title, String description) {}

    public record SuspendPartnerlinkRequest(@NotBlank String reason) {}

    public record PartnerlinkIdResponse(String id) {}

    public record PartnerlinkResponse(
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
