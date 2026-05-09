package com.hcl.hackathon.payment_processor.entity;

import com.hcl.hackathon.payment_processor.util.Currency;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_outcomes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOutcomesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private String debitAccountId;

    @Column(nullable = false)
    private String creditAccountId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private Currency currency;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private Instant processedAt;

    @Column(nullable = false)
    private long processingTimeMs;
}

