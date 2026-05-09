package com.hcl.hackathon.payment_processor.config;

import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Kafka Configuration for Producer and Consumer
 */
@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * Customize KafkaTemplate bean for message production
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        
        log.info("KafkaTemplate bean initialized");
        return template;
    }
}

