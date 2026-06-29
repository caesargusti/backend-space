package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(String userId);

    // TODO: Implementasikan method untuk mengambil Wallet berdasarkan userId dan menerapkan database lock agar race condition tidak terjadi.
    // Hint: Pertimbangkan menggunakan @Lock(LockModeType.PESSIMISTIC_WRITE) atau kemampuan serupa dari JpaRepository.
}
