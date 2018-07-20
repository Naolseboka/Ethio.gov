package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.ranking.ActivateRankingUseCase;
import et.gov.ethgov.search.application.ranking.CreateRankingUseCase;
import et.gov.ethgov.search.application.ranking.GetRankingUseCase;
import et.gov.ethgov.search.application.ranking.ListRankingUseCase;
import et.gov.ethgov.search.application.ranking.SuspendRankingUseCase;
import et.gov.ethgov.search.application.ranking.UpdateRankingUseCase;
import et.gov.ethgov.search.domain.ranking.Ranking;
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
@RequestMapping("/v1/search/ranking")
public class RankingController {
    private final CreateRankingUseCase createUseCase;
    private final UpdateRankingUseCase updateUseCase;
    private final ActivateRankingUseCase activateUseCase;
    private final SuspendRankingUseCase suspendUseCase;
    private final GetRankingUseCase getUseCase;
    private final ListRankingUseCase listUseCase;

    public RankingController(
            CreateRankingUseCase createUseCase,
            UpdateRankingUseCase updateUseCase,
            ActivateRankingUseCase activateUseCase,
            SuspendRankingUseCase suspendUseCase,
            GetRankingUseCase getUseCase,
            ListRankingUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RankingIdResponse create(@Valid @RequestBody CreateRankingRequest request) {
        var id = createUseCase.execute(new CreateRankingUseCase.CreateRankingCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RankingIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RankingResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RankingResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RankingIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRankingRequest request) {
        var updated = updateUseCase.execute(new UpdateRankingUseCase.UpdateRankingCommand(
                null, request.title(), request.description(), null, id, null));
        return new RankingIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRankingUseCase.ActivateRankingCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRankingRequest request) {
        suspendUseCase.execute(new SuspendRankingUseCase.SuspendRankingCommand(
                null, null, null, null, id, request.reason()));
    }

    private RankingResponse toResponse(Ranking aggregate) {
        return new RankingResponse(
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

    public record CreateRankingRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRankingRequest(String title, String description) {}

    public record SuspendRankingRequest(@NotBlank String reason) {}

    public record RankingIdResponse(String id) {}

    public record RankingResponse(
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
