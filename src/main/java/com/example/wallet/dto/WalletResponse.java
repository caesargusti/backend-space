package com.example.wallet.dto;

import com.example.wallet.model.WalletStatus;
import java.math.BigDecimal;

public record WalletResponse(
    String userId,
    BigDecimal balance,
    WalletStatus status
) {
}
