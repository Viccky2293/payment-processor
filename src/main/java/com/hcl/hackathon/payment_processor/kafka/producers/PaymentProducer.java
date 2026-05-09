package com.hcl.hackathon.payment_processor.kafka.producers;

import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Simple Kafka Producer - Just sends messages to Kafka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducer {

    final private KafkaTemplate<String, String> kafkaTemplate;

    final private KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;

    @Value("${com.hcl.payment-processor.kafka.producers.topics.payment-processed}")
    private String paymentProcessedTopic;

    public void sendMessage(String topic, String message) {
        log.info("Publishing to topic [{}]: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }

    public void publishToPaymentProcessed(PaymentEvent event) {
        publishEvent(paymentProcessedTopic, event.getPaymentId(), event);
    }

    private void publishEvent(String topic, UUID key, PaymentEvent event) {
        log.info("Publishing payment event to topic [{}] with key [{}]: {}", topic, key, event);
        paymentEventKafkaTemplate.send(topic, key.toString(), event);
    }
}

