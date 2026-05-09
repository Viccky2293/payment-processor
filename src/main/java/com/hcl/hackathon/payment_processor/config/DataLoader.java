package com.hcl.hackathon.payment_processor.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hcl.hackathon.payment_processor.entity.Accounts;
import com.hcl.hackathon.payment_processor.entity.PaymentOutcomesEntity;
import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;
import com.hcl.hackathon.payment_processor.kafka.producers.PaymentProducer;
import com.hcl.hackathon.payment_processor.repository.AccountRepository;
import com.hcl.hackathon.payment_processor.repository.PaymentOutcomesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    final private AccountRepository accountRepository;
    final private PaymentOutcomesRepository paymentOutcomesRepository;
    final private PaymentProducer paymentProducer;

    @Value("${com.hcl.payment-processor.kafka.consumers.payment-submit.topic-name}")
    private String paymentSubmitTopic;

    @Override
    public void run(String... args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        loadAccountRecords(mapper);
        loadPaymentRecords(mapper);
    }

    private void loadPaymentRecords(ObjectMapper mapper) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/sample-payments.json")) {
            if (inputStream != null) {
                List<PaymentEvent> paymentOutcomesEntities = mapper.readValue(inputStream, new TypeReference<>() {});
                paymentOutcomesEntities.forEach(p ->
                {
                    try {
                        paymentProducer.sendMessage(paymentSubmitTopic, mapper.writeValueAsString(p));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
                System.out.println("Payments loaded: " + paymentOutcomesEntities.size() + " records saved to H2.");
            } else {
                System.out.println("ample-payments.json file not found!");
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadAccountRecords(ObjectMapper mapper) {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/accounts.json")) {
            if (inputStream != null) {
                List<Accounts> accounts = mapper.readValue(inputStream, new TypeReference<>() {});
                accountRepository.saveAll(accounts);
                System.out.println("Accounts loaded: " + accounts.size() + " records saved to H2.");
            } else {
                System.out.println("accounts.json file not found!");
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }
}
