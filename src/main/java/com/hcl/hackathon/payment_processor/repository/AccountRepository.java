package com.hcl.hackathon.payment_processor.repository;

import com.hcl.hackathon.payment_processor.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Accounts, Long>  {
}
