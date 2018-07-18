package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.query.ActivateQueryUseCase;
import et.gov.ethgov.search.application.query.CreateQueryUseCase;
import et.gov.ethgov.search.application.query.GetQueryUseCase;
import et.gov.ethgov.search.application.query.ListQueryUseCase;
import et.gov.ethgov.search.application.query.SuspendQueryUseCase;
import et.gov.ethgov.search.application.query.UpdateQueryUseCase;
import et.gov.ethgov.search.domain.query.Query;
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
@RequestMapping("/v1/search/query")
public class QueryController {
    private final CreateQueryUseCase createUseCase;
    private final UpdateQueryUseCase updateUseCase;
    private final ActivateQueryUseCase activateUseCase;
    private final SuspendQueryUseCase suspendUseCase;
    private final GetQueryUseCase getUseCase;
    private final ListQueryUseCase listUseCase;

    public QueryController(
            CreateQueryUseCase createUseCase,
            UpdateQueryUseCase updateUseCase,
            ActivateQueryUseCase activateUseCase,
            SuspendQueryUseCase suspendUseCase,
            GetQueryUseCase getUseCase,
            ListQueryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QueryIdResponse create(@Valid @RequestBody CreateQueryRequest request) {
        var id = createUseCase.execute(new CreateQueryUseCase.CreateQueryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new QueryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public QueryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<QueryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public QueryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateQueryRequest request) {
        var updated = updateUseCase.execute(new UpdateQueryUseCase.UpdateQueryCommand(
                null, request.title(), request.description(), null, id, null));
        return new QueryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateQueryUseCase.ActivateQueryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendQueryRequest request) {
        suspendUseCase.execute(new SuspendQueryUseCase.SuspendQueryCommand(
                null, null, null, null, id, request.reason()));
    }

    private QueryResponse toResponse(Query aggregate) {
        return new QueryResponse(
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

    public record CreateQueryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateQueryRequest(String title, String description) {}

    public record SuspendQueryRequest(@NotBlank String reason) {}

    public record QueryIdResponse(String id) {}

    public record QueryResponse(
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
