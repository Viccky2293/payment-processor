package com.hcl.hackathon.payment_processor.service.impl;

import com.hcl.hackathon.payment_processor.entity.PaymentOutcomesEntity;
import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import com.hcl.hackathon.payment_processor.kafka.producers.PaymentProducer;
import com.hcl.hackathon.payment_processor.repository.PaymentOutcomesRepository;
import com.hcl.hackathon.payment_processor.service.PaymentProcessorService;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessorServiceImpl implements PaymentProcessorService {

    BigDecimal MAXIMUM_PAYMENT_AMOUNT = new BigDecimal("250000.00");

    private final PaymentOutcomesRepository paymentOutcomesRepository;
    private final PaymentProducer paymentEventProducer;

    @Override
    public void processPayment(PaymentEvent paymentEvent) {
        log.info("Processed payment request for paymentId: " + paymentEvent.getPaymentId() +
                ", amount: " + paymentEvent.getAmount() + " " + paymentEvent.getCurrency());

        try {
            if (paymentEvent.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Payment amount is valid.");
                var paymentOutcomesEntity = buildPaymentOutcomes(paymentEvent);
                paymentOutcomesRepository.save(paymentOutcomesEntity);
                log.info("Payment outcome saved for paymentId: " + paymentEvent.getPaymentId() + " with status: " + paymentOutcomesEntity.getStatus());
                paymentEventProducer.publishToPaymentProcessed(paymentEvent);
                log.info("Published payment processed event for paymentId: " + paymentEvent.getPaymentId());
            } else {
                log.error("Payment amount is invalid. Amount must be greater than 0.");
                throw new IllegalArgumentException("Payment amount is invalid. Amount must be greater than 0.");
            }
        } catch (Exception e) {
            log.error("Error processing payment request for paymentId: " + paymentEvent.getPaymentId() + ". Error: " + e.getMessage(), e);
        }
    }

    private PaymentOutcomesEntity buildPaymentOutcomes(PaymentEvent paymentEvent) {
        return PaymentOutcomesEntity.builder()
                .paymentId(paymentEvent.getPaymentId())
                .debitAccountId(paymentEvent.getDebitAccountId())
                .creditAccountId(paymentEvent.getCreditAccountId())
                .amount(paymentEvent.getAmount())
                .currency(paymentEvent.getCurrency())
                .status(determinePaymentStatus(paymentEvent.getAmount()))
                .processedAt(paymentEvent.getTimestamp())
                .processingTimeMs(Instant.now().toEpochMilli() - paymentEvent.getTimestamp().toEpochMilli())
                .build();
    }

    private PaymentStatus determinePaymentStatus(BigDecimal amount) {
        if (amount.compareTo(MAXIMUM_PAYMENT_AMOUNT) > 0) {
            return PaymentStatus.HELD;
        } else {
            return PaymentStatus.PROCESSED;
        }
    }
}
