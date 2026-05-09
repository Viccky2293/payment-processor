package com.hcl.hackathon.payment_processor.service.impl;

import com.hcl.hackathon.payment_processor.entity.PaymentOutcomesEntity;
import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import com.hcl.hackathon.payment_processor.kafka.producers.PaymentProducer;
import com.hcl.hackathon.payment_processor.repository.PaymentOutcomesRepository;
import com.hcl.hackathon.payment_processor.util.Currency;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorServiceImplTest {

    @Mock
    private PaymentOutcomesRepository paymentOutcomesRepository;

    @Mock
    private PaymentProducer paymentEventProducer;

    @InjectMocks
    private PaymentProcessorServiceImpl paymentProcessorService;

    private PaymentEvent paymentEvent;

    @BeforeEach
    void setUp() {
        paymentEvent = PaymentEvent.builder()
                .paymentId(UUID.randomUUID())
                .debitAccountId("20-15-88/43917265")
                .creditAccountId("30-91-44/87654321")
                .amount(new BigDecimal("4750.00"))
                .currency(Currency.GBP)
                .reference("INVOICE INV-10044")
                .timestamp(Instant.now())
                .build();
    }

    @Test
    void testProcessPayment_ValidAmount_StatusProcessed() {
        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(1)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(1)).publishToPaymentProcessed(paymentEvent);
    }

    @Test
    void testProcessPayment_ZeroAmount_ThrowsException() {
        paymentEvent.setAmount(BigDecimal.ZERO);

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(0)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(0)).publishToPaymentProcessed(any());
    }

    @Test
    void testProcessPayment_NegativeAmount_ThrowsException() {
        paymentEvent.setAmount(new BigDecimal("-100.00"));

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(0)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(0)).publishToPaymentProcessed(any());
    }

    @Test
    void testProcessPayment_AmountExceedsMaximum_StatusHeld() {
        paymentEvent.setAmount(new BigDecimal("300000.00"));

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(1)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(1)).publishToPaymentProcessed(paymentEvent);
    }

    @Test
    void testProcessPayment_SmallAmount_StatusProcessed() {
        paymentEvent.setAmount(new BigDecimal("100.00"));

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(1)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(1)).publishToPaymentProcessed(paymentEvent);
    }

    @Test
    void testProcessPayment_MaximumAmountExactly_StatusProcessed() {
        paymentEvent.setAmount(new BigDecimal("250000.00"));

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(1)).save(any(PaymentOutcomesEntity.class));
        verify(paymentEventProducer, times(1)).publishToPaymentProcessed(paymentEvent);
    }

    @Test
    void testProcessPayment_RepositoryException_HandlesGracefully() {
        paymentEvent.setAmount(new BigDecimal("1000.00"));
        org.mockito.Mockito.when(paymentOutcomesRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        paymentProcessorService.processPayment(paymentEvent);

        verify(paymentOutcomesRepository, times(1)).save(any(PaymentOutcomesEntity.class));
    }
}

