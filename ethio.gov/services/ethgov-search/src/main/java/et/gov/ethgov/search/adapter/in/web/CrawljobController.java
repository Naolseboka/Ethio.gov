package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.crawljob.ActivateCrawljobUseCase;
import et.gov.ethgov.search.application.crawljob.CreateCrawljobUseCase;
import et.gov.ethgov.search.application.crawljob.GetCrawljobUseCase;
import et.gov.ethgov.search.application.crawljob.ListCrawljobUseCase;
import et.gov.ethgov.search.application.crawljob.SuspendCrawljobUseCase;
import et.gov.ethgov.search.application.crawljob.UpdateCrawljobUseCase;
import et.gov.ethgov.search.domain.crawljob.Crawljob;
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
@RequestMapping("/v1/search/crawljob")
public class CrawljobController {
    private final CreateCrawljobUseCase createUseCase;
    private final UpdateCrawljobUseCase updateUseCase;
    private final ActivateCrawljobUseCase activateUseCase;
    private final SuspendCrawljobUseCase suspendUseCase;
    private final GetCrawljobUseCase getUseCase;
    private final ListCrawljobUseCase listUseCase;

    public CrawljobController(
            CreateCrawljobUseCase createUseCase,
            UpdateCrawljobUseCase updateUseCase,
            ActivateCrawljobUseCase activateUseCase,
            SuspendCrawljobUseCase suspendUseCase,
            GetCrawljobUseCase getUseCase,
            ListCrawljobUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CrawljobIdResponse create(@Valid @RequestBody CreateCrawljobRequest request) {
        var id = createUseCase.execute(new CreateCrawljobUseCase.CreateCrawljobCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CrawljobIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CrawljobResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CrawljobResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CrawljobIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCrawljobRequest request) {
        var updated = updateUseCase.execute(new UpdateCrawljobUseCase.UpdateCrawljobCommand(
                null, request.title(), request.description(), null, id, null));
        return new CrawljobIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCrawljobUseCase.ActivateCrawljobCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCrawljobRequest request) {
        suspendUseCase.execute(new SuspendCrawljobUseCase.SuspendCrawljobCommand(
                null, null, null, null, id, request.reason()));
    }

    private CrawljobResponse toResponse(Crawljob aggregate) {
        return new CrawljobResponse(
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

    public record CreateCrawljobRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCrawljobRequest(String title, String description) {}

    public record SuspendCrawljobRequest(@NotBlank String reason) {}

    public record CrawljobIdResponse(String id) {}

    public record CrawljobResponse(
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
