package com.hcl.hackathon.payment_processor.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accounts {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "account_id", nullable = false, unique = true)
        private String accountId;

        @Column(name = "account_name", nullable = false)
        private String accountName;

        @Column(name = "account_type")
        private String accountType;

        @Column(name = "status")
        private String status;

        @Column(name = "currency")
        private String currency;

        @Column(name = "opened_date")
        private LocalDate openedDate;
}
