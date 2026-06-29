package com.example.wallet.repository;

import com.example.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.example.wallet.dto.UserSpendingReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletUserIdOrderByTimestampDesc(String userId);

    // TODO: Uncomment method di bawah dan tulis query JPQL menggunakan @Query untuk mencari high spenders.
    // Query harus:
    // 1. Melakukan JOIN antara entitas Wallet dan Transaction.
    // 2. Memfilter transaksi dengan type='SPEND', description (merchant category) = :category, dan timestamp di antara :start dan :end.
    // 3. Melakukan GROUP BY berdasarkan userId pada wallet.
    // 4. Memfilter hasil grup (HAVING) saat total amount transaksi lebih besar atau sama dengan :threshold.
    // 5. Melakukan ORDER BY total amount transaksi secara descending.
    // 6. Memproyeksikan hasil ke list berisi instance record UserSpendingReport.
    //
    // List<UserSpendingReport> findHighSpendersByCategory(
    //         @Param("category") String category,
    //         @Param("start") LocalDateTime start,
    //         @Param("end") LocalDateTime end,
    //         @Param("threshold") BigDecimal threshold
    // );
}
