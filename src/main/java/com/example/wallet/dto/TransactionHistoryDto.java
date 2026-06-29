package com.example.wallet.dto;

import com.example.wallet.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryDto(
    Long id,
    BigDecimal amount,
    TransactionType type,
    LocalDateTime timestamp,
    String description
) {
}
