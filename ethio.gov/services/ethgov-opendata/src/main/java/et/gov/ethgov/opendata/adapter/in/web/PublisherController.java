package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.publisher.ActivatePublisherUseCase;
import et.gov.ethgov.opendata.application.publisher.CreatePublisherUseCase;
import et.gov.ethgov.opendata.application.publisher.GetPublisherUseCase;
import et.gov.ethgov.opendata.application.publisher.ListPublisherUseCase;
import et.gov.ethgov.opendata.application.publisher.SuspendPublisherUseCase;
import et.gov.ethgov.opendata.application.publisher.UpdatePublisherUseCase;
import et.gov.ethgov.opendata.domain.publisher.Publisher;
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
@RequestMapping("/v1/opendata/publisher")
public class PublisherController {
    private final CreatePublisherUseCase createUseCase;
    private final UpdatePublisherUseCase updateUseCase;
    private final ActivatePublisherUseCase activateUseCase;
    private final SuspendPublisherUseCase suspendUseCase;
    private final GetPublisherUseCase getUseCase;
    private final ListPublisherUseCase listUseCase;

    public PublisherController(
            CreatePublisherUseCase createUseCase,
            UpdatePublisherUseCase updateUseCase,
            ActivatePublisherUseCase activateUseCase,
            SuspendPublisherUseCase suspendUseCase,
            GetPublisherUseCase getUseCase,
            ListPublisherUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublisherIdResponse create(@Valid @RequestBody CreatePublisherRequest request) {
        var id = createUseCase.execute(new CreatePublisherUseCase.CreatePublisherCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PublisherIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PublisherResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PublisherResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PublisherIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePublisherRequest request) {
        var updated = updateUseCase.execute(new UpdatePublisherUseCase.UpdatePublisherCommand(
                null, request.title(), request.description(), null, id, null));
        return new PublisherIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePublisherUseCase.ActivatePublisherCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPublisherRequest request) {
        suspendUseCase.execute(new SuspendPublisherUseCase.SuspendPublisherCommand(
                null, null, null, null, id, request.reason()));
    }

    private PublisherResponse toResponse(Publisher aggregate) {
        return new PublisherResponse(
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

    public record CreatePublisherRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePublisherRequest(String title, String description) {}

    public record SuspendPublisherRequest(@NotBlank String reason) {}

    public record PublisherIdResponse(String id) {}

    public record PublisherResponse(
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
