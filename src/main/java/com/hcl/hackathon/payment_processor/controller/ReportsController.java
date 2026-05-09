package com.hcl.hackathon.payment_processor.controller;

import com.hcl.hackathon.payment_processor.kafka.producers.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportsController {

    final private PaymentProducer paymentProducer;

    @Value("${com.hcl.payment-processor.kafka.consumers.payment-submit.topic-name}")
    private String paymentSubmitTopic;

    @PostMapping("/testKafka")
    public String getReports(@RequestParam String message) {
        // Placeholder for actual report generation logic
        paymentProducer.sendMessage(paymentSubmitTopic, message);
        return "Reports data will be here";
    }
}
