package com.example.wallet.dto;

import java.math.BigDecimal;

public sealed interface TransactionRequest permits EarnRequest, SpendRequest, RefundRequest {
    BigDecimal amount();
}
