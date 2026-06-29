package com.example.wallet.dto;

import java.math.BigDecimal;

public record UserSpendingReport(
    String userId,
    BigDecimal totalSpent
) {
}
