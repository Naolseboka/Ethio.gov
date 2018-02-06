package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.title.ActivateTitleUseCase;
import et.gov.ethgov.land.application.title.CreateTitleUseCase;
import et.gov.ethgov.land.application.title.GetTitleUseCase;
import et.gov.ethgov.land.application.title.ListTitleUseCase;
import et.gov.ethgov.land.application.title.SuspendTitleUseCase;
import et.gov.ethgov.land.application.title.UpdateTitleUseCase;
import et.gov.ethgov.land.domain.title.Title;
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
@RequestMapping("/v1/land/title")
public class TitleController {
    private final CreateTitleUseCase createUseCase;
    private final UpdateTitleUseCase updateUseCase;
    private final ActivateTitleUseCase activateUseCase;
    private final SuspendTitleUseCase suspendUseCase;
    private final GetTitleUseCase getUseCase;
    private final ListTitleUseCase listUseCase;

    public TitleController(
            CreateTitleUseCase createUseCase,
            UpdateTitleUseCase updateUseCase,
            ActivateTitleUseCase activateUseCase,
            SuspendTitleUseCase suspendUseCase,
            GetTitleUseCase getUseCase,
            ListTitleUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TitleIdResponse create(@Valid @RequestBody CreateTitleRequest request) {
        var id = createUseCase.execute(new CreateTitleUseCase.CreateTitleCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TitleIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TitleResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TitleResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TitleIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTitleRequest request) {
        var updated = updateUseCase.execute(new UpdateTitleUseCase.UpdateTitleCommand(
                null, request.title(), request.description(), null, id, null));
        return new TitleIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTitleUseCase.ActivateTitleCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTitleRequest request) {
        suspendUseCase.execute(new SuspendTitleUseCase.SuspendTitleCommand(
                null, null, null, null, id, request.reason()));
    }

    private TitleResponse toResponse(Title aggregate) {
        return new TitleResponse(
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

    public record CreateTitleRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTitleRequest(String title, String description) {}

    public record SuspendTitleRequest(@NotBlank String reason) {}

    public record TitleIdResponse(String id) {}

    public record TitleResponse(
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
