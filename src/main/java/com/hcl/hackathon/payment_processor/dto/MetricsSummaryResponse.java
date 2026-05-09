package com.hcl.hackathon.payment_processor.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload for GET /api/metrics/summary.
 * Values are sourced from live in-memory AtomicLong counters, not the database.
 */
@Data
@Builder
public class MetricsSummaryResponse {

    // Total number of payments successfully processed since application start
    private long totalProcessed;

    // Total number of payments placed on hold since application start
    private long totalHeld;
}

