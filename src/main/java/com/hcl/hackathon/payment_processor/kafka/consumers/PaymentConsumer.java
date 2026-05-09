package com.hcl.hackathon.payment_processor.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import com.hcl.hackathon.payment_processor.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    final private PaymentProcessorService processorService;

    final private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${com.hcl.payment-processor.kafka.consumers.payment-submit.topic-name}",
            groupId = "${com.hcl.payment-processor.kafka.consumers.payment-submit.group-id}"
    )
    public void consume(String message) throws JsonProcessingException {
        log.info("Received the payment processing message: {}", message);
        PaymentEvent paymentEvent = objectMapper.readValue(message, PaymentEvent.class);

        log.info("Deserialized payment: {}", paymentEvent);
        // Process the payment DTO

        try {
            processPaymentRequest(paymentEvent);
        } catch (Exception e) {
            log.error("Error processing payment request event [{}]: {}", paymentEvent.getPaymentId(), e.getMessage(), e);
        }

    }

    private void processPaymentRequest(PaymentEvent event) {
        log.info("Processing payment request for paymentId [{}], amount [{}] {}",
                event.getPaymentId(), event.getAmount(), event.getCurrency());
        processorService.processPayment(event);
    }

}
