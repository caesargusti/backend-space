package com.example.wallet.controller;

import com.example.wallet.dto.*;
import com.example.wallet.model.TransactionType;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// TODO: Tambahkan anotasi class ini sebagai REST Controller dengan base path "/api/wallets"
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // TODO: Implementasikan GET /{userId}
    // Mengambil detail wallet untuk user. Return HTTP 200 dengan WalletResponse.
    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@RequestParam String userId) {
        Wallet data = walletService.getWallet(userId);
        return ResponseEntity.ok(mapToResponse(data));
    }

    // TODO: Implementasikan POST /{userId}/earn
    // Menambahkan poin ke wallet user.
    // Validasi request body menggunakan anotasi Jakarta validation. Return HTTP 200 dengan WalletResponse.
    public ResponseEntity<WalletResponse> earnPoints(String userId, EarnRequest request) {
        return null;
    }

    // TODO: Implementasikan POST /{userId}/spend
    // Mengurangi poin dari wallet user.
    // Validasi request body. Return HTTP 200 dengan WalletResponse.
    public ResponseEntity<WalletResponse> spendPoints(String userId, SpendRequest request) {
        return null;
    }

    // TODO: Implementasikan GET /{userId}/summary
    // Return ringkasan transaksi (amount yang sudah diagregasi per type). Return HTTP 200.
    public ResponseEntity<Map<TransactionType, BigDecimal>> getTransactionSummary(String userId) {
        return null;
    }

    // TODO: Implementasikan GET /{userId}/history
    // Return daftar riwayat transaksi. Return HTTP 200.
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(String userId) {
        return null;
    }

    // TODO: Implementasikan GET /reports/high-spenders
    // Return daftar spending report. Return HTTP 200.
    // Query parameter: category (String), start (LocalDateTime), end (LocalDateTime), threshold (BigDecimal).
    // Hint: Kamu bisa pakai @RequestParam dan @DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) untuk tanggal.
    public ResponseEntity<List<UserSpendingReport>> getHighSpenders(
            String category,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end,
            BigDecimal threshold
    ) {
        return null;
    }

    // Helper mapper method (opsional untuk digunakan)
    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(wallet.getUserId(), wallet.getBalance(), wallet.getStatus());
    }
}
