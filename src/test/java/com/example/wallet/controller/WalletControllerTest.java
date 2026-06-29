package com.example.wallet.controller;

import com.example.wallet.dto.EarnRequest;
import com.example.wallet.dto.SpendRequest;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.WalletBlockedException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletStatus;
import com.example.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetWallet_Success() throws Exception {
        Wallet wallet = new Wallet("user1", BigDecimal.valueOf(100.00), WalletStatus.ACTIVE);
        when(walletService.getWallet("user1")).thenReturn(wallet);

        mockMvc.perform(get("/api/wallets/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetWallet_NotFound() throws Exception {
        when(walletService.getWallet("user-invalid")).thenThrow(new WalletNotFoundException("Wallet not found for user: user-invalid"));

        mockMvc.perform(get("/api/wallets/user-invalid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WALLET_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Wallet not found for user: user-invalid"));
    }

    @Test
    void testEarnPoints_Success() throws Exception {
        Wallet wallet = new Wallet("user1", BigDecimal.valueOf(150.00), WalletStatus.ACTIVE);
        EarnRequest request = new EarnRequest(BigDecimal.valueOf(50.00), "Promotion");
        
        when(walletService.earnPoints(eq("user1"), any(EarnRequest.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/wallets/user1/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00));
    }

    @Test
    void testEarnPoints_ValidationFailed() throws Exception {
        // Invalid amount (negative)
        EarnRequest request = new EarnRequest(BigDecimal.valueOf(-10.00), "Promotion");

        mockMvc.perform(post("/api/wallets/user1/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void testSpendPoints_InsufficientFunds() throws Exception {
        SpendRequest request = new SpendRequest(BigDecimal.valueOf(150.00), "Electronics");
        when(walletService.spendPoints(eq("user1"), any(SpendRequest.class)))
                .thenThrow(new InsufficientFundsException("Insufficient funds in wallet"));

        mockMvc.perform(post("/api/wallets/user1/spend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void testSpendPoints_WalletBlocked() throws Exception {
        SpendRequest request = new SpendRequest(BigDecimal.valueOf(50.00), "Food");
        when(walletService.spendPoints(eq("user1"), any(SpendRequest.class)))
                .thenThrow(new WalletBlockedException("Wallet is blocked"));

        mockMvc.perform(post("/api/wallets/user1/spend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("WALLET_BLOCKED"));
    }

    @Test
    void testSpendPoints_InvalidArgument() throws Exception {
        SpendRequest request = new SpendRequest(BigDecimal.valueOf(0.50), "Microtransaction");
        when(walletService.spendPoints(eq("user1"), any(SpendRequest.class)))
                .thenThrow(new IllegalArgumentException("Spend amount is below minimum limit"));

        mockMvc.perform(post("/api/wallets/user1/spend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    void testGetHighSpenders_Success() throws Exception {
        java.util.List<com.example.wallet.dto.UserSpendingReport> mockReport = java.util.List.of(
                new com.example.wallet.dto.UserSpendingReport("userB", BigDecimal.valueOf(150.00)),
                new com.example.wallet.dto.UserSpendingReport("userA", BigDecimal.valueOf(90.00))
        );

        when(walletService.getHighSpendersReport(
                eq("Food"),
                any(java.time.LocalDateTime.class),
                any(java.time.LocalDateTime.class),
                eq(BigDecimal.valueOf(50.00))
        )).thenReturn(mockReport);

        mockMvc.perform(get("/api/wallets/reports/high-spenders")
                        .param("category", "Food")
                        .param("start", "2026-06-30T00:00:00")
                        .param("end", "2026-06-30T23:59:59")
                        .param("threshold", "50.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("userB"))
                .andExpect(jsonPath("$[0].totalSpent").value(150.00))
                .andExpect(jsonPath("$[1].userId").value("userA"))
                .andExpect(jsonPath("$[1].totalSpent").value(90.00));
    }
}
