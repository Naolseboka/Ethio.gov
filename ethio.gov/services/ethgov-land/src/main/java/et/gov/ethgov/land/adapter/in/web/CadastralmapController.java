package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.cadastralmap.ActivateCadastralmapUseCase;
import et.gov.ethgov.land.application.cadastralmap.CreateCadastralmapUseCase;
import et.gov.ethgov.land.application.cadastralmap.GetCadastralmapUseCase;
import et.gov.ethgov.land.application.cadastralmap.ListCadastralmapUseCase;
import et.gov.ethgov.land.application.cadastralmap.SuspendCadastralmapUseCase;
import et.gov.ethgov.land.application.cadastralmap.UpdateCadastralmapUseCase;
import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
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
@RequestMapping("/v1/land/cadastralmap")
public class CadastralmapController {
    private final CreateCadastralmapUseCase createUseCase;
    private final UpdateCadastralmapUseCase updateUseCase;
    private final ActivateCadastralmapUseCase activateUseCase;
    private final SuspendCadastralmapUseCase suspendUseCase;
    private final GetCadastralmapUseCase getUseCase;
    private final ListCadastralmapUseCase listUseCase;

    public CadastralmapController(
            CreateCadastralmapUseCase createUseCase,
            UpdateCadastralmapUseCase updateUseCase,
            ActivateCadastralmapUseCase activateUseCase,
            SuspendCadastralmapUseCase suspendUseCase,
            GetCadastralmapUseCase getUseCase,
            ListCadastralmapUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastralmapIdResponse create(@Valid @RequestBody CreateCadastralmapRequest request) {
        var id = createUseCase.execute(new CreateCadastralmapUseCase.CreateCadastralmapCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CadastralmapIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CadastralmapResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CadastralmapResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CadastralmapIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCadastralmapRequest request) {
        var updated = updateUseCase.execute(new UpdateCadastralmapUseCase.UpdateCadastralmapCommand(
                null, request.title(), request.description(), null, id, null));
        return new CadastralmapIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCadastralmapUseCase.ActivateCadastralmapCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCadastralmapRequest request) {
        suspendUseCase.execute(new SuspendCadastralmapUseCase.SuspendCadastralmapCommand(
                null, null, null, null, id, request.reason()));
    }

    private CadastralmapResponse toResponse(Cadastralmap aggregate) {
        return new CadastralmapResponse(
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

    public record CreateCadastralmapRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCadastralmapRequest(String title, String description) {}

    public record SuspendCadastralmapRequest(@NotBlank String reason) {}

    public record CadastralmapIdResponse(String id) {}

    public record CadastralmapResponse(
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
