package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.signature.ActivateSignatureUseCase;
import et.gov.ethgov.document.application.signature.CreateSignatureUseCase;
import et.gov.ethgov.document.application.signature.GetSignatureUseCase;
import et.gov.ethgov.document.application.signature.ListSignatureUseCase;
import et.gov.ethgov.document.application.signature.SuspendSignatureUseCase;
import et.gov.ethgov.document.application.signature.UpdateSignatureUseCase;
import et.gov.ethgov.document.domain.signature.Signature;
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
@RequestMapping("/v1/document/signature")
public class SignatureController {
    private final CreateSignatureUseCase createUseCase;
    private final UpdateSignatureUseCase updateUseCase;
    private final ActivateSignatureUseCase activateUseCase;
    private final SuspendSignatureUseCase suspendUseCase;
    private final GetSignatureUseCase getUseCase;
    private final ListSignatureUseCase listUseCase;

    public SignatureController(
            CreateSignatureUseCase createUseCase,
            UpdateSignatureUseCase updateUseCase,
            ActivateSignatureUseCase activateUseCase,
            SuspendSignatureUseCase suspendUseCase,
            GetSignatureUseCase getUseCase,
            ListSignatureUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SignatureIdResponse create(@Valid @RequestBody CreateSignatureRequest request) {
        var id = createUseCase.execute(new CreateSignatureUseCase.CreateSignatureCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SignatureIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SignatureResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SignatureResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SignatureIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSignatureRequest request) {
        var updated = updateUseCase.execute(new UpdateSignatureUseCase.UpdateSignatureCommand(
                null, request.title(), request.description(), null, id, null));
        return new SignatureIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSignatureUseCase.ActivateSignatureCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSignatureRequest request) {
        suspendUseCase.execute(new SuspendSignatureUseCase.SuspendSignatureCommand(
                null, null, null, null, id, request.reason()));
    }

    private SignatureResponse toResponse(Signature aggregate) {
        return new SignatureResponse(
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

    public record CreateSignatureRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSignatureRequest(String title, String description) {}

    public record SuspendSignatureRequest(@NotBlank String reason) {}

    public record SignatureIdResponse(String id) {}

    public record SignatureResponse(
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
