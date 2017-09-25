package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.educationrecord.ActivateEducationrecordUseCase;
import et.gov.ethgov.citizen.application.educationrecord.CreateEducationrecordUseCase;
import et.gov.ethgov.citizen.application.educationrecord.GetEducationrecordUseCase;
import et.gov.ethgov.citizen.application.educationrecord.ListEducationrecordUseCase;
import et.gov.ethgov.citizen.application.educationrecord.SuspendEducationrecordUseCase;
import et.gov.ethgov.citizen.application.educationrecord.UpdateEducationrecordUseCase;
import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
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
@RequestMapping("/v1/citizen/educationrecord")
public class EducationrecordController {
    private final CreateEducationrecordUseCase createUseCase;
    private final UpdateEducationrecordUseCase updateUseCase;
    private final ActivateEducationrecordUseCase activateUseCase;
    private final SuspendEducationrecordUseCase suspendUseCase;
    private final GetEducationrecordUseCase getUseCase;
    private final ListEducationrecordUseCase listUseCase;

    public EducationrecordController(
            CreateEducationrecordUseCase createUseCase,
            UpdateEducationrecordUseCase updateUseCase,
            ActivateEducationrecordUseCase activateUseCase,
            SuspendEducationrecordUseCase suspendUseCase,
            GetEducationrecordUseCase getUseCase,
            ListEducationrecordUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EducationrecordIdResponse create(@Valid @RequestBody CreateEducationrecordRequest request) {
        var id = createUseCase.execute(new CreateEducationrecordUseCase.CreateEducationrecordCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EducationrecordIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EducationrecordResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EducationrecordResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EducationrecordIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEducationrecordRequest request) {
        var updated = updateUseCase.execute(new UpdateEducationrecordUseCase.UpdateEducationrecordCommand(
                null, request.title(), request.description(), null, id, null));
        return new EducationrecordIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEducationrecordUseCase.ActivateEducationrecordCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEducationrecordRequest request) {
        suspendUseCase.execute(new SuspendEducationrecordUseCase.SuspendEducationrecordCommand(
                null, null, null, null, id, request.reason()));
    }

    private EducationrecordResponse toResponse(Educationrecord aggregate) {
        return new EducationrecordResponse(
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

    public record CreateEducationrecordRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEducationrecordRequest(String title, String description) {}

    public record SuspendEducationrecordRequest(@NotBlank String reason) {}

    public record EducationrecordIdResponse(String id) {}

    public record EducationrecordResponse(
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
