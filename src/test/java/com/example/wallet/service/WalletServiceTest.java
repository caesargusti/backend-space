package com.example.wallet.service;

import com.example.wallet.config.WalletProperties;
import com.example.wallet.dto.*;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.WalletBlockedException;
import com.example.wallet.model.TransactionType;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletStatus;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletProperties walletProperties;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void testConfigurationPropertiesLoaded() {
        assertNotNull(walletProperties);
        assertEquals(0, new BigDecimal("1.00").compareTo(walletProperties.getMinSpendAmount()));
        assertEquals(0, new BigDecimal("10000.00").compareTo(walletProperties.getMaxSpendAmount()));
        assertNotNull(walletProperties.getTierMultipliers());
        assertEquals(0, new BigDecimal("1.1").compareTo(walletProperties.getTierMultipliers().get("silver")));
    }

    @Test
    void testEarnPoints_NewWalletCreated() {
        Wallet wallet = walletService.earnPoints("user1", new EarnRequest(BigDecimal.valueOf(100.00), "Welcome Bonus"));
        
        assertNotNull(wallet);
        assertEquals("user1", wallet.getUserId());
        assertEquals(0, new BigDecimal("100.00").compareTo(wallet.getBalance()));
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());

        var transactions = transactionRepository.findByWalletUserIdOrderByTimestampDesc("user1");
        assertEquals(1, transactions.size());
        assertEquals(TransactionType.EARN, transactions.get(0).getType());
        assertEquals(0, new BigDecimal("100.00").compareTo(transactions.get(0).getAmount()));
        assertEquals("Welcome Bonus", transactions.get(0).getDescription());
    }

    @Test
    void testEarnPoints_ExistingWalletUpdated() {
        walletRepository.save(new Wallet("user1", BigDecimal.valueOf(50.00), WalletStatus.ACTIVE));

        Wallet wallet = walletService.earnPoints("user1", new EarnRequest(BigDecimal.valueOf(100.00), "Promotion Earn"));

        assertEquals(0, new BigDecimal("150.00").compareTo(wallet.getBalance()));
    }

    @Test
    void testSpendPoints_Success() {
        walletRepository.save(new Wallet("user1", BigDecimal.valueOf(200.00), WalletStatus.ACTIVE));

        Wallet wallet = walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(50.00), "Electronics"));

        assertEquals(0, new BigDecimal("150.00").compareTo(wallet.getBalance()));

        var transactions = transactionRepository.findByWalletUserIdOrderByTimestampDesc("user1");
        assertEquals(1, transactions.size());
        assertEquals(TransactionType.SPEND, transactions.get(0).getType());
        assertEquals(0, new BigDecimal("50.00").compareTo(transactions.get(0).getAmount()));
    }

    @Test
    void testSpendPoints_InsufficientFunds() {
        walletRepository.save(new Wallet("user1", BigDecimal.valueOf(20.00), WalletStatus.ACTIVE));

        assertThrows(InsufficientFundsException.class, () -> 
            walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(50.00), "Food"))
        );
    }

    @Test
    void testSpendPoints_WalletBlocked() {
        walletRepository.save(new Wallet("user1", BigDecimal.valueOf(200.00), WalletStatus.BLOCKED));

        assertThrows(WalletBlockedException.class, () -> 
            walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(50.00), "Food"))
        );
    }

    @Test
    void testSpendPoints_LimitViolations() {
        walletRepository.save(new Wallet("user1", BigDecimal.valueOf(20000.00), WalletStatus.ACTIVE));

        // Under minimum spend (1.00)
        assertThrows(IllegalArgumentException.class, () -> 
            walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(0.50), "Microtrans"))
        );

        // Over maximum spend (10000.00)
        assertThrows(IllegalArgumentException.class, () -> 
            walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(10001.00), "Car purchase"))
        );
    }

    @Test
    void testCalculateProcessingFee() {
        // Earn -> 0
        assertEquals(BigDecimal.ZERO, walletService.calculateProcessingFee(new EarnRequest(BigDecimal.valueOf(50), "Campaign")));

        // Spend <= 100 -> 0.50
        assertEquals(0, new BigDecimal("0.50").compareTo(walletService.calculateProcessingFee(new SpendRequest(BigDecimal.valueOf(50), "Category"))));
        assertEquals(0, new BigDecimal("0.50").compareTo(walletService.calculateProcessingFee(new SpendRequest(BigDecimal.valueOf(100), "Category"))));

        // Spend > 100 -> 1%
        assertEquals(0, new BigDecimal("1.50").compareTo(walletService.calculateProcessingFee(new SpendRequest(BigDecimal.valueOf(150), "Category"))));

        // Refund -> 2%
        assertEquals(0, new BigDecimal("1.00").compareTo(walletService.calculateProcessingFee(new RefundRequest(BigDecimal.valueOf(50), "Tx1"))));
    }

    @Test
    void testGetTransactionSummary() {
        Wallet wallet = walletRepository.save(new Wallet("user1", BigDecimal.valueOf(500.00), WalletStatus.ACTIVE));
        
        walletService.earnPoints("user1", new EarnRequest(BigDecimal.valueOf(100), "Earn 1"));
        walletService.spendPoints("user1", new SpendRequest(BigDecimal.valueOf(50), "Spend 1"));
        walletService.earnPoints("user1", new EarnRequest(BigDecimal.valueOf(200), "Earn 2"));
        
        transactionRepository.save(new com.example.wallet.model.Transaction(wallet, BigDecimal.valueOf(30), TransactionType.REFUND, java.time.LocalDateTime.now(), "Refund 1"));

        Map<TransactionType, BigDecimal> summary = walletService.getTransactionSummary("user1");

        // Earn sum: 100 + 200 = 300
        // Spend sum: 50
        // Refund sum: 30
        assertNotNull(summary);
        assertEquals(0, new BigDecimal("300").compareTo(summary.get(TransactionType.EARN)));
        assertEquals(0, new BigDecimal("50").compareTo(summary.get(TransactionType.SPEND)));
        assertEquals(0, new BigDecimal("30").compareTo(summary.get(TransactionType.REFUND)));
    }

    @Test
    void testConcurrentSpend_ThreadSafety() throws InterruptedException {
        String userId = "concurrentUser";
        walletRepository.save(new Wallet(userId, BigDecimal.valueOf(1000.00), WalletStatus.ACTIVE));

        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch finishedLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    walletService.spendPoints(userId, new SpendRequest(BigDecimal.valueOf(10.00), "Merchant"));
                } catch (Exception e) {
                    // Ignore exception to see if any lock timeout or validation failed,
                    // but we expect all threads to succeed if properly serialised/locked.
                } finally {
                    finishedLatch.countDown();
                }
            });
        }

        latch.countDown();
        finishedLatch.await();
        executor.shutdown();

        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow();
        assertEquals(0, new BigDecimal("900.00").compareTo(wallet.getBalance()), 
            "Race conditions occurred! Stale balance: " + wallet.getBalance());
    }

    @Test
    void testHighSpendersReport() {
        Wallet w1 = walletRepository.save(new Wallet("userA", BigDecimal.valueOf(1000), WalletStatus.ACTIVE));
        Wallet w2 = walletRepository.save(new Wallet("userB", BigDecimal.valueOf(1000), WalletStatus.ACTIVE));
        Wallet w3 = walletRepository.save(new Wallet("userC", BigDecimal.valueOf(1000), WalletStatus.ACTIVE));
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // userA spends: 50 Food, 40 Food (total 90)
        transactionRepository.save(new com.example.wallet.model.Transaction(w1, BigDecimal.valueOf(50), TransactionType.SPEND, now, "Food"));
        transactionRepository.save(new com.example.wallet.model.Transaction(w1, BigDecimal.valueOf(40), TransactionType.SPEND, now.minusMinutes(10), "Food"));
        
        // userB spends: 150 Food (total 150)
        transactionRepository.save(new com.example.wallet.model.Transaction(w2, BigDecimal.valueOf(150), TransactionType.SPEND, now, "Food"));
        
        // userC spends: 20 Food, 120 Electronics (total 20 Food)
        transactionRepository.save(new com.example.wallet.model.Transaction(w3, BigDecimal.valueOf(20), TransactionType.SPEND, now, "Food"));
        transactionRepository.save(new com.example.wallet.model.Transaction(w3, BigDecimal.valueOf(120), TransactionType.SPEND, now, "Electronics"));

        // Call report with category "Food", threshold 50
        var report = walletService.getHighSpendersReport(
                "Food",
                now.minusHours(1),
                now.plusHours(1),
                BigDecimal.valueOf(50)
        );

        // Expect: userB (150) then userA (90). userC (20) and Electronics category should be excluded.
        assertNotNull(report);
        assertEquals(2, report.size());
        
        assertEquals("userB", report.get(0).userId());
        assertEquals(0, BigDecimal.valueOf(150).compareTo(report.get(0).totalSpent()));
        
        assertEquals("userA", report.get(1).userId());
        assertEquals(0, BigDecimal.valueOf(90).compareTo(report.get(1).totalSpent()));
    }
}
