package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.category.ActivateCategoryUseCase;
import et.gov.ethgov.licensing.application.category.CreateCategoryUseCase;
import et.gov.ethgov.licensing.application.category.GetCategoryUseCase;
import et.gov.ethgov.licensing.application.category.ListCategoryUseCase;
import et.gov.ethgov.licensing.application.category.SuspendCategoryUseCase;
import et.gov.ethgov.licensing.application.category.UpdateCategoryUseCase;
import et.gov.ethgov.licensing.domain.category.Category;
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
@RequestMapping("/v1/licensing/category")
public class CategoryController {
    private final CreateCategoryUseCase createUseCase;
    private final UpdateCategoryUseCase updateUseCase;
    private final ActivateCategoryUseCase activateUseCase;
    private final SuspendCategoryUseCase suspendUseCase;
    private final GetCategoryUseCase getUseCase;
    private final ListCategoryUseCase listUseCase;

    public CategoryController(
            CreateCategoryUseCase createUseCase,
            UpdateCategoryUseCase updateUseCase,
            ActivateCategoryUseCase activateUseCase,
            SuspendCategoryUseCase suspendUseCase,
            GetCategoryUseCase getUseCase,
            ListCategoryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryIdResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        var id = createUseCase.execute(new CreateCategoryUseCase.CreateCategoryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CategoryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CategoryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CategoryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCategoryRequest request) {
        var updated = updateUseCase.execute(new UpdateCategoryUseCase.UpdateCategoryCommand(
                null, request.title(), request.description(), null, id, null));
        return new CategoryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCategoryUseCase.ActivateCategoryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCategoryRequest request) {
        suspendUseCase.execute(new SuspendCategoryUseCase.SuspendCategoryCommand(
                null, null, null, null, id, request.reason()));
    }

    private CategoryResponse toResponse(Category aggregate) {
        return new CategoryResponse(
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

    public record CreateCategoryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCategoryRequest(String title, String description) {}

    public record SuspendCategoryRequest(@NotBlank String reason) {}

    public record CategoryIdResponse(String id) {}

    public record CategoryResponse(
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
