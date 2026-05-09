package com.hcl.hackathon.payment_processor.service;


import com.hcl.hackathon.payment_processor.dto.MetricsSummaryResponse;
import com.hcl.hackathon.payment_processor.dto.PagedActivityResponse;
import com.hcl.hackathon.payment_processor.dto.PaymentOutcomeResponse;
import com.hcl.hackathon.payment_processor.dto.ReportSummaryResponse;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;

import java.util.List;
public interface PaymentReportService {
    MetricsSummaryResponse getMetricsSummary();
    ReportSummaryResponse getReportSummary();
    PagedActivityResponse getActivity(PaymentStatus status, String accountId, int page, int size);
    List<PaymentOutcomeResponse> getAccountHistory(String accountId);
}
