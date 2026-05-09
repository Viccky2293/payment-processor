package com.hcl.hackathon.payment_processor.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Wrapper returned by GET /api/reports/activity.
 * Mirrors Spring's Page structure without exposing the JPA Page directly to the API layer.
 */
@Data
@Builder
public class PagedActivityResponse {

    // The current page number (0-based)
    private int page;

    // Number of records per page
    private int size;

    // Total number of records matching the applied filters
    private long totalElements;

    // Total number of pages available
    private int totalPages;

    // Payment outcome records for the current page
    private List<PaymentOutcomeResponse> content;
}

