package com.example.wallet.service;

import com.example.wallet.config.WalletProperties;
import com.example.wallet.dto.*;
import com.example.wallet.exception.*;
import com.example.wallet.model.*;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletProperties walletProperties;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository,
                         WalletProperties walletProperties) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletProperties = walletProperties;
    }

    /**
        * Mengambil wallet user saat ini.
     */
    public Wallet getWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
    }

    /**
     * Exercise 3a: Implementasi earnPoints.
     * Method ini harus menambahkan poin ke wallet. Jika wallet user belum ada, buat wallet baru dengan status aktif.
     * Harus berjalan dalam boundary transaksi.
     * Catat transaksi dengan tipe EARN.
     */
    // TODO: Tambahkan anotasi untuk boundary transaksi pada method ini
    public Wallet earnPoints(String userId, EarnRequest request) {
        // TODO: Implementasikan logika earn
        // 1. Ambil atau buat wallet (status default: ACTIVE, saldo awal default: 0)
        // 2. Tambahkan amount dari request ke saldo wallet
        // 3. Simpan wallet
        // 4. Simpan entity Transaction baru untuk mencatat operasi ini (tipe: EARN, deskripsi: campaign)
        return null;
    }

    /**
     * Exercise 3b: Implementasi spendPoints.
     * Method ini mengurangi poin dari wallet.
     * Aturan:
     * - Wallet harus ada dan statusnya harus ACTIVE (bukan BLOCKED). Jika tidak, throw WalletBlockedException atau WalletNotFoundException.
     * - Amount spend harus berada di antara walletProperties.getMinSpendAmount() dan walletProperties.getMaxSpendAmount() (inklusif).
     *   Jika tidak, throw IllegalArgumentException.
     * - Saldo wallet harus mencukupi. Jika tidak, throw InsufficientFundsException.
     * - Catat transaksi dengan tipe SPEND.
     * 
     * Aturan konkurensi:
     * - Pastikan method ini aman untuk operasi spend/earn yang berjalan bersamaan pada wallet yang sama.
     * - Hint: Gunakan locking saat membaca data dari repository.
     */
    // TODO: Tambahkan anotasi untuk boundary transaksi pada method ini
    public Wallet spendPoints(String userId, SpendRequest request) {
        // TODO: Implementasikan logika spend
        // 1. Ambil wallet dengan lock (contoh: Pessimistic Write) untuk mencegah race condition
        // 2. Lakukan validasi (wallet ada, status aktif, rentang amount, saldo mencukupi)
        // 3. Kurangi saldo wallet dengan amount dari request
        // 4. Simpan wallet
        // 5. Simpan entity Transaction baru untuk mencatat operasi ini (tipe: SPEND, deskripsi: merchant category)
        return null;
    }

    /**
     * Exercise 1: Hitung processing fee menggunakan Java 21 Pattern Matching switch.
     * Aturan:
     * - EarnRequest: fee selalu BigDecimal.ZERO.
     * - SpendRequest:
     *   - Jika amount lebih dari 100, fee adalah 1% dari amount (amount * 0.01).
     *   - Jika tidak, fee flat 0.50 (BigDecimal.valueOf(0.50)).
     * - RefundRequest: fee adalah 2% dari amount (amount * 0.02).
     */
    public BigDecimal calculateProcessingFee(TransactionRequest request) {
        // TODO: Implementasikan menggunakan ekspresi switch pattern matching Java 21.
        // Hint: Lakukan switch pada parameter request, cocokkan berdasarkan record type. Gunakan klausa "when" untuk pengecekan amount.
        if (request instanceof SpendRequest spendRequest) {
            if (spendRequest.amount().compareTo(BigDecimal.valueOf(100)) > 0) {
                return spendRequest.amount().multiply(BigDecimal.valueOf(0.01));
            } else {
                return BigDecimal.valueOf(0.50);
            }
        } else if (request instanceof RefundRequest refundRequest) {
            return refundRequest.amount().multiply(BigDecimal.valueOf(0.02));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Exercise 2: Ambil riwayat transaksi dan hitung summary menggunakan Java Streams.
     * Return map yang berisi total amount transaksi per TransactionType untuk user tertentu.
     * Hint: Ambil transaksi dari database, group berdasarkan type, lalu jumlahkan amount dengan Collectors.
     */
    public Map<TransactionType, BigDecimal> getTransactionSummary(String userId) {
        // TODO: Implementasikan agregasi stream
        return Map.of();
    }

    /**
        * Mengambil riwayat transaksi.
     */
    public List<TransactionHistoryDto> getTransactionHistory(String userId) {
        return transactionRepository.findByWalletUserIdOrderByTimestampDesc(userId).stream()
                .map(t -> new TransactionHistoryDto(
                        t.getId(),
                        t.getAmount(),
                        t.getType(),
                        t.getTimestamp(),
                        t.getDescription()
                ))
                .toList();
    }

    /**
        * Exercise 4: Mengambil spending report untuk high spenders.
        * Method ini mendelegasikan query kustom ke TransactionRepository.
     */
    public List<UserSpendingReport> getHighSpendersReport(
            String category,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal threshold
    ) {
        // TODO: Panggil method query kustom di transaction repository untuk mengambil high spenders report.
        return List.of();
    }
}
