package com.hcl.hackathon.payment_processor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response payload for GET /api/reports/summary.
 * Aggregated from the payment_outcomes table via JPQL queries.
 */
@Data
@Builder
public class ReportSummaryResponse {

    // Count of payments with status PROCESSED
    private long totalProcessed;

    // Count of payments with status HELD
    private long totalHeld;

    // Count of payments with status REJECTED
    private long totalRejected;

    // Sum of amount for all PROCESSED payments
    private BigDecimal totalAmountProcessed;

    // Timestamp of the earliest record in payment_outcomes
    private Instant rangeFrom;

    // Timestamp of the most recent record in payment_outcomes
    private Instant rangeTo;
}

