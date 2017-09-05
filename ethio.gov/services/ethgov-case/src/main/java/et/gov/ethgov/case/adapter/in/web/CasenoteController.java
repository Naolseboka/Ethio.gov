package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.casenote.ActivateCasenoteUseCase;
import et.gov.ethgov.case.application.casenote.CreateCasenoteUseCase;
import et.gov.ethgov.case.application.casenote.GetCasenoteUseCase;
import et.gov.ethgov.case.application.casenote.ListCasenoteUseCase;
import et.gov.ethgov.case.application.casenote.SuspendCasenoteUseCase;
import et.gov.ethgov.case.application.casenote.UpdateCasenoteUseCase;
import et.gov.ethgov.case.domain.casenote.Casenote;
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
@RequestMapping("/v1/case/casenote")
public class CasenoteController {
    private final CreateCasenoteUseCase createUseCase;
    private final UpdateCasenoteUseCase updateUseCase;
    private final ActivateCasenoteUseCase activateUseCase;
    private final SuspendCasenoteUseCase suspendUseCase;
    private final GetCasenoteUseCase getUseCase;
    private final ListCasenoteUseCase listUseCase;

    public CasenoteController(
            CreateCasenoteUseCase createUseCase,
            UpdateCasenoteUseCase updateUseCase,
            ActivateCasenoteUseCase activateUseCase,
            SuspendCasenoteUseCase suspendUseCase,
            GetCasenoteUseCase getUseCase,
            ListCasenoteUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CasenoteIdResponse create(@Valid @RequestBody CreateCasenoteRequest request) {
        var id = createUseCase.execute(new CreateCasenoteUseCase.CreateCasenoteCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CasenoteIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CasenoteResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CasenoteResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CasenoteIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCasenoteRequest request) {
        var updated = updateUseCase.execute(new UpdateCasenoteUseCase.UpdateCasenoteCommand(
                null, request.title(), request.description(), null, id, null));
        return new CasenoteIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCasenoteUseCase.ActivateCasenoteCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCasenoteRequest request) {
        suspendUseCase.execute(new SuspendCasenoteUseCase.SuspendCasenoteCommand(
                null, null, null, null, id, request.reason()));
    }

    private CasenoteResponse toResponse(Casenote aggregate) {
        return new CasenoteResponse(
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

    public record CreateCasenoteRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCasenoteRequest(String title, String description) {}

    public record SuspendCasenoteRequest(@NotBlank String reason) {}

    public record CasenoteIdResponse(String id) {}

    public record CasenoteResponse(
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
