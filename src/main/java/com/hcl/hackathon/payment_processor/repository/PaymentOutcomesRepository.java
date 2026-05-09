package com.hcl.hackathon.payment_processor.repository;

import com.hcl.hackathon.payment_processor.entity.PaymentOutcomesEntity;
import com.hcl.hackathon.payment_processor.util.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentOutcomesRepository extends JpaRepository<PaymentOutcomesEntity, Long> {

    Optional<PaymentOutcomesEntity> findByPaymentId(UUID paymentId);

    // Paginated list filtered by status — used by /api/reports/activity?status=
    Page<PaymentOutcomesEntity> findByStatus(PaymentStatus status, Pageable pageable);

    // Paginated full list (no status filter) — used by /api/reports/activity with no filter
    Page<PaymentOutcomesEntity> findAll(Pageable pageable);

    // Paginated filter by status AND either debit or credit account — supports combined ?status=&accountId= filter
    @Query("SELECT p FROM PaymentOutcomesEntity p WHERE p.status = :status AND (p.debitAccountId = :accountId OR p.creditAccountId = :accountId)")
    Page<PaymentOutcomesEntity> findByStatusAndAccount(@Param("status") PaymentStatus status, @Param("accountId") String accountId, Pageable pageable);

    // Paginated filter by accountId only (debit or credit) — supports ?accountId= filter without status
    @Query("SELECT p FROM PaymentOutcomesEntity p WHERE p.debitAccountId = :accountId OR p.creditAccountId = :accountId")
    Page<PaymentOutcomesEntity> findByAccount(@Param("accountId") String accountId, Pageable pageable);

    // All outcomes for an account ordered most-recent first — used by /api/accounts/{accountId}/history
    @Query("SELECT p FROM PaymentOutcomesEntity p WHERE p.debitAccountId = :accountId OR p.creditAccountId = :accountId ORDER BY p.processedAt DESC")
    List<PaymentOutcomesEntity> findAccountHistory(@Param("accountId") String accountId);

    // Count of records grouped by status — used by /api/reports/summary
    @Query("SELECT p.status, COUNT(p) FROM PaymentOutcomesEntity p GROUP BY p.status")
    List<Object[]> countByStatus();

    // Total amount of PROCESSED payments — used by /api/reports/summary
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentOutcomesEntity p WHERE p.status = 'PROCESSED'")
    BigDecimal sumProcessedAmount();

    // Earliest record timestamp — used for date-range in /api/reports/summary
    @Query("SELECT MIN(p.processedAt) FROM PaymentOutcomesEntity p")
    Instant findEarliestProcessedAt();

    // Latest record timestamp — used for date-range in /api/reports/summary
    @Query("SELECT MAX(p.processedAt) FROM PaymentOutcomesEntity p")
    Instant findLatestProcessedAt();
}
