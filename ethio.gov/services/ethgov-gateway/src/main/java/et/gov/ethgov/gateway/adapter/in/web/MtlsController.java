package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.mtls.ActivateMtlsUseCase;
import et.gov.ethgov.gateway.application.mtls.CreateMtlsUseCase;
import et.gov.ethgov.gateway.application.mtls.GetMtlsUseCase;
import et.gov.ethgov.gateway.application.mtls.ListMtlsUseCase;
import et.gov.ethgov.gateway.application.mtls.SuspendMtlsUseCase;
import et.gov.ethgov.gateway.application.mtls.UpdateMtlsUseCase;
import et.gov.ethgov.gateway.domain.mtls.Mtls;
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
@RequestMapping("/v1/gateway/mtls")
public class MtlsController {
    private final CreateMtlsUseCase createUseCase;
    private final UpdateMtlsUseCase updateUseCase;
    private final ActivateMtlsUseCase activateUseCase;
    private final SuspendMtlsUseCase suspendUseCase;
    private final GetMtlsUseCase getUseCase;
    private final ListMtlsUseCase listUseCase;

    public MtlsController(
            CreateMtlsUseCase createUseCase,
            UpdateMtlsUseCase updateUseCase,
            ActivateMtlsUseCase activateUseCase,
            SuspendMtlsUseCase suspendUseCase,
            GetMtlsUseCase getUseCase,
            ListMtlsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MtlsIdResponse create(@Valid @RequestBody CreateMtlsRequest request) {
        var id = createUseCase.execute(new CreateMtlsUseCase.CreateMtlsCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MtlsIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MtlsResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MtlsResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MtlsIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMtlsRequest request) {
        var updated = updateUseCase.execute(new UpdateMtlsUseCase.UpdateMtlsCommand(
                null, request.title(), request.description(), null, id, null));
        return new MtlsIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMtlsUseCase.ActivateMtlsCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMtlsRequest request) {
        suspendUseCase.execute(new SuspendMtlsUseCase.SuspendMtlsCommand(
                null, null, null, null, id, request.reason()));
    }

    private MtlsResponse toResponse(Mtls aggregate) {
        return new MtlsResponse(
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

    public record CreateMtlsRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMtlsRequest(String title, String description) {}

    public record SuspendMtlsRequest(@NotBlank String reason) {}

    public record MtlsIdResponse(String id) {}

    public record MtlsResponse(
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
