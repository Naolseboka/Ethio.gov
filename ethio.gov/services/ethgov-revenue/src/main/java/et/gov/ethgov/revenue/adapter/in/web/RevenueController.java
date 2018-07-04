package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.bill.CancelBillUseCase;
import et.gov.ethgov.revenue.application.bill.CreateBillUseCase;
import et.gov.ethgov.revenue.application.bill.GetBillUseCase;
import et.gov.ethgov.revenue.application.bill.IssueBillUseCase;
import et.gov.ethgov.revenue.application.bill.MarkOverdueUseCase;
import et.gov.ethgov.revenue.application.bill.MarkPaidUseCase;
import et.gov.ethgov.revenue.application.bill.UpdateBillUseCase;
import et.gov.ethgov.revenue.application.bill.WriteOffBillUseCase;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/bills")
public class RevenueController {

    private final CreateBillUseCase createBill;
    private final UpdateBillUseCase updateBill;
    private final IssueBillUseCase issueBill;
    private final MarkOverdueUseCase markOverdue;
    private final MarkPaidUseCase markPaid;
    private final CancelBillUseCase cancelBill;
    private final WriteOffBillUseCase writeOffBill;
    private final GetBillUseCase getBill;

    public RevenueController(
            CreateBillUseCase createBill,
            UpdateBillUseCase updateBill,
            IssueBillUseCase issueBill,
            MarkOverdueUseCase markOverdue,
            MarkPaidUseCase markPaid,
            CancelBillUseCase cancelBill,
            WriteOffBillUseCase writeOffBill,
            GetBillUseCase getBill) {
        this.createBill = createBill;
        this.updateBill = updateBill;
        this.issueBill = issueBill;
        this.markOverdue = markOverdue;
        this.markPaid = markPaid;
        this.cancelBill = cancelBill;
        this.writeOffBill = writeOffBill;
        this.getBill = getBill;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillIdResponse create(@Valid @RequestBody CreateBillRequest request) {
        return new BillIdResponse(createBill.execute(new CreateBillUseCase.Command(
                        request.tenantId(),
                        request.type(),
                        request.description(),
                        request.citizenId(),
                        request.organizationId(),
                        request.amountEtb(),
                        request.periodStart(),
                        request.periodEnd(),
                        request.licenseId(),
                        request.permitId(),
                        request.parcelId(),
                        request.linkedCaseId()))
                .value());
    }

    @GetMapping("/lookup")
    public BillResponse lookup(@RequestParam String tenantId, @RequestParam String billNumber) {
        return BillResponse.from(getBill.byNumber(tenantId, billNumber));
    }

    @GetMapping("/{billId}")
    public BillResponse get(@PathVariable String billId) {
        return BillResponse.from(getBill.byId(billId));
    }

    @PutMapping("/{billId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String billId, @Valid @RequestBody UpdateBillRequest request) {
        updateBill.execute(new UpdateBillUseCase.Command(
                billId,
                request.description(),
                request.amountEtb(),
                request.periodStart(),
                request.periodEnd(),
                request.licenseId(),
                request.permitId(),
                request.parcelId(),
                request.linkedCaseId()));
    }

    @PostMapping("/{billId}/issue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issue(@PathVariable String billId, @Valid @RequestBody IssueRequest request) {
        issueBill.execute(billId, request.dueAt());
    }

    @PostMapping("/{billId}/overdue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void overdue(@PathVariable String billId) {
        markOverdue.execute(billId);
    }

    @PostMapping("/{billId}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pay(@PathVariable String billId, @Valid @RequestBody PayRequest request) {
        markPaid.execute(billId, request.paymentReference());
    }

    @PostMapping("/{billId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String billId, @Valid @RequestBody ReasonRequest request) {
        cancelBill.execute(billId, request.reason());
    }

    @PostMapping("/{billId}/write-off")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void writeOff(@PathVariable String billId, @Valid @RequestBody ReasonRequest request) {
        writeOffBill.execute(billId, request.reason());
    }

    public record CreateBillRequest(
            @NotBlank String tenantId,
            @NotBlank String type,
            @NotBlank String description,
            String citizenId,
            String organizationId,
            @NotBlank String amountEtb,
            String periodStart,
            String periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId) {}

    public record UpdateBillRequest(
            @NotBlank String description,
            @NotBlank String amountEtb,
            String periodStart,
            String periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId) {}

    public record IssueRequest(@NotBlank String dueAt) {}

    public record PayRequest(@NotBlank @Size(min = 5, max = 120) String paymentReference) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record BillIdResponse(String billId) {}

    public record BillResponse(
            String billId,
            String tenantId,
            String billNumber,
            String type,
            String description,
            String status,
            String citizenId,
            String organizationId,
            String amountEtb,
            String periodStart,
            String periodEnd,
            String dueAt,
            String issuedAt,
            String paidAt,
            String paymentReference,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId) {

        static BillResponse from(RevenueBill bill) {
            return new BillResponse(
                    bill.id().value(),
                    bill.tenantId().value(),
                    bill.billNumber().value(),
                    bill.type().name(),
                    bill.description(),
                    bill.status().name(),
                    bill.party().citizenId().orElse(null),
                    bill.party().organizationId().orElse(null),
                    bill.amount().amount().toPlainString(),
                    bill.periodStart().map(java.time.Instant::toString).orElse(null),
                    bill.periodEnd().map(java.time.Instant::toString).orElse(null),
                    bill.dueAt().map(java.time.Instant::toString).orElse(null),
                    bill.issuedAt().map(java.time.Instant::toString).orElse(null),
                    bill.paidAt().map(java.time.Instant::toString).orElse(null),
                    bill.paymentReference().orElse(null),
                    bill.licenseId().orElse(null),
                    bill.permitId().orElse(null),
                    bill.parcelId().orElse(null),
                    bill.linkedCaseId().orElse(null));
        }
    }
}
