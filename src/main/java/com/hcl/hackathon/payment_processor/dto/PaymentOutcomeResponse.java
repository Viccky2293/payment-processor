package com.hcl.hackathon.payment_processor.dto;

import com.hcl.hackathon.payment_processor.util.Currency;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single row returned in paginated activity and account history responses.
 * Maps directly from PaymentOutcomesEntity — keeps the API contract decoupled from the entity.
 */
@Data
@Builder
public class PaymentOutcomeResponse {

    private Long id;
    private UUID paymentId;
    private String debitAccountId;
    private String creditAccountId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;
    private Instant processedAt;
    private long processingTimeMs;
}

