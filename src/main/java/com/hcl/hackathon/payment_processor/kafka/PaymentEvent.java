package com.hcl.hackathon.payment_processor.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcl.hackathon.payment_processor.util.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentEvent {

    @JsonProperty("paymentId")
    private UUID paymentId;

    @JsonProperty("debitAccountId")
    private String debitAccountId;

    @JsonProperty("creditAccountId")
    private String creditAccountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private Currency currency;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("timestamp")
    private Instant timestamp;
}

