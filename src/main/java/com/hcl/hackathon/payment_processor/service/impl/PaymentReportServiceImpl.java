package com.hcl.hackathon.payment_processor.service.impl;

import com.hcl.hackathon.payment_processor.dto.MetricsSummaryResponse;
import com.hcl.hackathon.payment_processor.dto.PagedActivityResponse;
import com.hcl.hackathon.payment_processor.dto.PaymentOutcomeResponse;
import com.hcl.hackathon.payment_processor.dto.ReportSummaryResponse;
import com.hcl.hackathon.payment_processor.entity.PaymentOutcomesEntity;
import com.hcl.hackathon.payment_processor.repository.PaymentOutcomesRepository;
import com.hcl.hackathon.payment_processor.service.PaymentReportService;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReportServiceImpl implements PaymentReportService {

    private final PaymentOutcomesRepository paymentOutcomesRepository;

    @Override
    public MetricsSummaryResponse getMetricsSummary() {
        var map = paymentOutcomesRepository.findAll().stream().collect(Collectors.groupingBy(PaymentOutcomesEntity::getStatus));

        return MetricsSummaryResponse.builder()
                .totalProcessed(map.get(PaymentStatus.PROCESSED).size())
                .totalHeld(map.get(PaymentStatus.HELD).size())
                .build();
    }

    @Override
    public ReportSummaryResponse getReportSummary() {
        List<Object[]> statusCounts = paymentOutcomesRepository.countByStatus();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            countMap.put(row[0].toString(), (Long) row[1]);
        }
        return ReportSummaryResponse.builder()
                .totalProcessed(countMap.getOrDefault(PaymentStatus.PROCESSED.name(), 0L))
                .totalHeld(countMap.getOrDefault(PaymentStatus.HELD.name(), 0L))
                .totalRejected(countMap.getOrDefault(PaymentStatus.REJECTED.name(), 0L))
                .totalAmountProcessed(paymentOutcomesRepository.sumProcessedAmount() != null
                        ? paymentOutcomesRepository.sumProcessedAmount() : BigDecimal.ZERO)
                .rangeFrom(paymentOutcomesRepository.findEarliestProcessedAt())
                .rangeTo(paymentOutcomesRepository.findLatestProcessedAt())
                .build();
    }

    @Override
    public PagedActivityResponse getActivity(PaymentStatus status, String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "processedAt"));
        Page<PaymentOutcomesEntity> result;

        if (status != null && accountId != null) {
            result = paymentOutcomesRepository.findByStatusAndAccount(status, accountId, pageable);
        } else if (status != null) {
            result = paymentOutcomesRepository.findByStatus(status, pageable);
        } else if (accountId != null) {
            result = paymentOutcomesRepository.findByAccount(accountId, pageable);
        } else {
            result = paymentOutcomesRepository.findAll(pageable);
        }

        return PagedActivityResponse.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<PaymentOutcomeResponse> getAccountHistory(String accountId) {
        return paymentOutcomesRepository.findAccountHistory(accountId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentOutcomeResponse toResponse(PaymentOutcomesEntity entity) {
        return PaymentOutcomeResponse.builder()
                .id(entity.getId())
                .paymentId(entity.getPaymentId())
                .debitAccountId(entity.getDebitAccountId())
                .creditAccountId(entity.getCreditAccountId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .processedAt(entity.getProcessedAt())
                .processingTimeMs(entity.getProcessingTimeMs())
                .build();
    }
}