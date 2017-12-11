package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.gwraterule.ActivateGwrateruleUseCase;
import et.gov.ethgov.gateway.application.gwraterule.CreateGwrateruleUseCase;
import et.gov.ethgov.gateway.application.gwraterule.GetGwrateruleUseCase;
import et.gov.ethgov.gateway.application.gwraterule.ListGwrateruleUseCase;
import et.gov.ethgov.gateway.application.gwraterule.SuspendGwrateruleUseCase;
import et.gov.ethgov.gateway.application.gwraterule.UpdateGwrateruleUseCase;
import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
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
@RequestMapping("/v1/gateway/gwraterule")
public class GwrateruleController {
    private final CreateGwrateruleUseCase createUseCase;
    private final UpdateGwrateruleUseCase updateUseCase;
    private final ActivateGwrateruleUseCase activateUseCase;
    private final SuspendGwrateruleUseCase suspendUseCase;
    private final GetGwrateruleUseCase getUseCase;
    private final ListGwrateruleUseCase listUseCase;

    public GwrateruleController(
            CreateGwrateruleUseCase createUseCase,
            UpdateGwrateruleUseCase updateUseCase,
            ActivateGwrateruleUseCase activateUseCase,
            SuspendGwrateruleUseCase suspendUseCase,
            GetGwrateruleUseCase getUseCase,
            ListGwrateruleUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GwrateruleIdResponse create(@Valid @RequestBody CreateGwrateruleRequest request) {
        var id = createUseCase.execute(new CreateGwrateruleUseCase.CreateGwrateruleCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new GwrateruleIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public GwrateruleResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<GwrateruleResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public GwrateruleIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateGwrateruleRequest request) {
        var updated = updateUseCase.execute(new UpdateGwrateruleUseCase.UpdateGwrateruleCommand(
                null, request.title(), request.description(), null, id, null));
        return new GwrateruleIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateGwrateruleUseCase.ActivateGwrateruleCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendGwrateruleRequest request) {
        suspendUseCase.execute(new SuspendGwrateruleUseCase.SuspendGwrateruleCommand(
                null, null, null, null, id, request.reason()));
    }

    private GwrateruleResponse toResponse(Gwraterule aggregate) {
        return new GwrateruleResponse(
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

    public record CreateGwrateruleRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateGwrateruleRequest(String title, String description) {}

    public record SuspendGwrateruleRequest(@NotBlank String reason) {}

    public record GwrateruleIdResponse(String id) {}

    public record GwrateruleResponse(
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
