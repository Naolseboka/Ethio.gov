package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.ocrjob.ActivateOcrjobUseCase;
import et.gov.ethgov.document.application.ocrjob.CreateOcrjobUseCase;
import et.gov.ethgov.document.application.ocrjob.GetOcrjobUseCase;
import et.gov.ethgov.document.application.ocrjob.ListOcrjobUseCase;
import et.gov.ethgov.document.application.ocrjob.SuspendOcrjobUseCase;
import et.gov.ethgov.document.application.ocrjob.UpdateOcrjobUseCase;
import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
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
@RequestMapping("/v1/document/ocrjob")
public class OcrjobController {
    private final CreateOcrjobUseCase createUseCase;
    private final UpdateOcrjobUseCase updateUseCase;
    private final ActivateOcrjobUseCase activateUseCase;
    private final SuspendOcrjobUseCase suspendUseCase;
    private final GetOcrjobUseCase getUseCase;
    private final ListOcrjobUseCase listUseCase;

    public OcrjobController(
            CreateOcrjobUseCase createUseCase,
            UpdateOcrjobUseCase updateUseCase,
            ActivateOcrjobUseCase activateUseCase,
            SuspendOcrjobUseCase suspendUseCase,
            GetOcrjobUseCase getUseCase,
            ListOcrjobUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OcrjobIdResponse create(@Valid @RequestBody CreateOcrjobRequest request) {
        var id = createUseCase.execute(new CreateOcrjobUseCase.CreateOcrjobCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OcrjobIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OcrjobResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OcrjobResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OcrjobIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOcrjobRequest request) {
        var updated = updateUseCase.execute(new UpdateOcrjobUseCase.UpdateOcrjobCommand(
                null, request.title(), request.description(), null, id, null));
        return new OcrjobIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOcrjobUseCase.ActivateOcrjobCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOcrjobRequest request) {
        suspendUseCase.execute(new SuspendOcrjobUseCase.SuspendOcrjobCommand(
                null, null, null, null, id, request.reason()));
    }

    private OcrjobResponse toResponse(Ocrjob aggregate) {
        return new OcrjobResponse(
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

    public record CreateOcrjobRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOcrjobRequest(String title, String description) {}

    public record SuspendOcrjobRequest(@NotBlank String reason) {}

    public record OcrjobIdResponse(String id) {}

    public record OcrjobResponse(
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
