package com.hcl.hackathon.payment_processor.controller;

import com.hcl.hackathon.payment_processor.dto.MetricsSummaryResponse;
import com.hcl.hackathon.payment_processor.dto.PagedActivityResponse;
import com.hcl.hackathon.payment_processor.dto.PaymentOutcomeResponse;
import com.hcl.hackathon.payment_processor.dto.ReportSummaryResponse;
import com.hcl.hackathon.payment_processor.service.PaymentReportService;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentProcessorController {

    private final PaymentReportService paymentReportService;

    /**
     * GET /api/metrics/summary
     * Returns live in-memory counters (totalProcessed, totalHeld, totalRejected, avgProcessingTimeMs).
     * Values are updated in real time by the Kafka consumer — no DB query involved.
     */
    @GetMapping("/metrics/summary")
    public ResponseEntity<MetricsSummaryResponse> getMetricsSummary() {
        return ResponseEntity.ok(paymentReportService.getMetricsSummary());
    }

    /**
     * GET /api/reports/summary
     * Aggregated view from the payment_outcomes table: counts per status,
     * total PROCESSED amount, and the earliest-to-latest date range of all records.
     */
    @GetMapping("/reports/summary")
    public ResponseEntity<ReportSummaryResponse> getReportSummary() {
        return ResponseEntity.ok(paymentReportService.getReportSummary());
    }

    /**
     * GET /api/reports/activity
     * Paginated list of payment outcomes. Supports optional filters:
     *   ?status=PROCESSED|HELD|REJECTED  — filter by outcome status
     *   ?accountId=ACC123                — filter by debit or credit account
     *   ?page=0&size=20                  — pagination controls (defaults: page=0, size=20)
     */
    @GetMapping("/reports/activity")
    public ResponseEntity<PagedActivityResponse> getActivityReport(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentReportService.getActivity(status, accountId, page, size));
    }

    /**
     * GET /api/accounts/{accountId}/history
     * Returns all payment outcomes where the account appears as either the debit
     * or credit party, ordered most-recent first.
     */
    @GetMapping("/accounts/{accountId}/history")
    public ResponseEntity<List<PaymentOutcomeResponse>> getAccountHistory(
            @PathVariable String accountId) {
        return ResponseEntity.ok(paymentReportService.getAccountHistory(accountId));
    }
}
