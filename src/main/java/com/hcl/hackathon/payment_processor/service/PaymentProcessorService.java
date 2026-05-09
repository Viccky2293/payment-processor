package com.hcl.hackathon.payment_processor.service;


import com.hcl.hackathon.payment_processor.kafka.PaymentEvent;

public interface PaymentProcessorService {
    void processPayment(PaymentEvent event);
}
