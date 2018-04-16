package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.mandate.ActivateMandateUseCase;
import et.gov.ethgov.organization.application.mandate.CreateMandateUseCase;
import et.gov.ethgov.organization.application.mandate.GetMandateUseCase;
import et.gov.ethgov.organization.application.mandate.ListMandateUseCase;
import et.gov.ethgov.organization.application.mandate.SuspendMandateUseCase;
import et.gov.ethgov.organization.application.mandate.UpdateMandateUseCase;
import et.gov.ethgov.organization.domain.mandate.Mandate;
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
@RequestMapping("/v1/organization/mandate")
public class MandateController {
    private final CreateMandateUseCase createUseCase;
    private final UpdateMandateUseCase updateUseCase;
    private final ActivateMandateUseCase activateUseCase;
    private final SuspendMandateUseCase suspendUseCase;
    private final GetMandateUseCase getUseCase;
    private final ListMandateUseCase listUseCase;

    public MandateController(
            CreateMandateUseCase createUseCase,
            UpdateMandateUseCase updateUseCase,
            ActivateMandateUseCase activateUseCase,
            SuspendMandateUseCase suspendUseCase,
            GetMandateUseCase getUseCase,
            ListMandateUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MandateIdResponse create(@Valid @RequestBody CreateMandateRequest request) {
        var id = createUseCase.execute(new CreateMandateUseCase.CreateMandateCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MandateIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MandateResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MandateResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MandateIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMandateRequest request) {
        var updated = updateUseCase.execute(new UpdateMandateUseCase.UpdateMandateCommand(
                null, request.title(), request.description(), null, id, null));
        return new MandateIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMandateUseCase.ActivateMandateCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMandateRequest request) {
        suspendUseCase.execute(new SuspendMandateUseCase.SuspendMandateCommand(
                null, null, null, null, id, request.reason()));
    }

    private MandateResponse toResponse(Mandate aggregate) {
        return new MandateResponse(
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

    public record CreateMandateRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMandateRequest(String title, String description) {}

    public record SuspendMandateRequest(@NotBlank String reason) {}

    public record MandateIdResponse(String id) {}

    public record MandateResponse(
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
