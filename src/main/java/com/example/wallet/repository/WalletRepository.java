package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(String userId);

    // TODO: Implementasikan method untuk mengambil Wallet berdasarkan userId dan menerapkan database lock agar race condition tidak terjadi.
    // Hint: Pertimbangkan menggunakan @Lock(LockModeType.PESSIMISTIC_WRITE) atau kemampuan serupa dari JpaRepository.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.userId = :userId")
    Optional<Wallet> findByUserIdForUpdate(@Param("userId") String userId);
}
